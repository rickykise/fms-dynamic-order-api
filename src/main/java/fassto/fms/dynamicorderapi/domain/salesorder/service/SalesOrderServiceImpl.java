package fassto.fms.dynamicorderapi.domain.salesorder.service;

import fassto.fms.dynamicorderapi.domain.config.dto.ConfigDetail;
import fassto.fms.dynamicorderapi.domain.config.dto.CwbConfig;
import fassto.fms.dynamicorderapi.domain.config.dto.CwbDetail;
import fassto.fms.dynamicorderapi.domain.config.dto.ZoneConfig;
import fassto.fms.dynamicorderapi.domain.config.enums.SettingType;
import fassto.fms.dynamicorderapi.domain.config.service.CustomerService;
import fassto.fms.dynamicorderapi.domain.salesorder.dto.connectivity.FailOrderRequest;
import fassto.fms.dynamicorderapi.domain.salesorder.dto.connectivity.SuccessOrderRequest;
import fassto.fms.dynamicorderapi.domain.salesorder.dto.fms.ReleaseOrder;
import fassto.fms.dynamicorderapi.domain.salesorder.dto.fms.ReleaseRequest;
import fassto.fms.dynamicorderapi.domain.salesorder.dto.connectivity.ReleaseResultRequest;
import fassto.fms.dynamicorderapi.domain.salesorder.mapper.SalesOrderMapper;
import fassto.fms.dynamicorderapi.domain.salesorder.vo.OrderGodVo;
import fassto.fms.dynamicorderapi.domain.salesorder.vo.OrderLineVo;
import fassto.fms.dynamicorderapi.global.enums.ConfigType;
import fassto.fms.dynamicorderapi.global.enums.GodType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

@Slf4j
@Service
@RequiredArgsConstructor
public class SalesOrderServiceImpl implements SalesOrderService {

    private final SalesOrderMapper releaseMapper;
    private final CustomerService customerService;

    // FMS에서 분리할 주문 리스트를 받아 주문 분리하여 connectivity에 보낼 형태로 변환
    @Override
    public ReleaseResultRequest release(ReleaseRequest request) {
        String whCd = request.getWhCd();
        List<ReleaseOrder> OrderList = request.getOrders();
        List<SuccessOrderRequest> successOrderList = new ArrayList<>();
        List<FailOrderRequest> failOrderList = new ArrayList<>();

        // 다이나믹 오더 분리
        for (ReleaseOrder order : OrderList) {
            try {
                String cstCd = order.getCstCd();
                List<ConfigDetail> configList = customerService.getCustomerDetailConfigUsed(whCd, cstCd);

                if (configList.isEmpty()) {
                    failOrderList.add(handleFailedOrder(order, "config 미설정"));
                } else {
                    List<OrderLineVo> orderLineVoList = releaseMapper.toOrderLines(order.getLines());

                    for (ConfigDetail configDetail : configList) {
                        if (ConfigType.ZONE.getType().equals(configDetail.getConfigType())) { // 존 기준 분리인 경우
                            orderLineVoList = processZoneConfig(configDetail, orderLineVoList);
                        }
                        else if (ConfigType.CWB.getType().equals(configDetail.getConfigType())) { // 완박스 분리인 경우
                            orderLineVoList = processCwbConfig(configDetail, orderLineVoList);
                        }
                    }

                    if (orderLineVoList.isEmpty()) { // 리스트가 비어있으면 주문 분리에 실패
                        failOrderList.add(handleFailedOrder(order, "zone config 미설정"));
                    } else { // 리스트에 값이 있으면 주문 분리에 성공
                        successOrderList.add(handleSuccessOrder(order, orderLineVoList));
                    }
                }
            } catch (Exception e) {
                // 예외 발생 시 로그 출력 및 실패 리스트 추가
                log.error("주문 처리 중 오류 발생 (orderId: {}, slipNo: {}): {}", request.getId(), order.getOutOrdSlipNo(), e.getMessage(), e);
                failOrderList.add(handleFailedOrder(order, "주문 처리 중 오류 발생"));
            }
            // 주문 하나 끝
        }

        return new ReleaseResultRequest(
                request.getId(),
                request.getWhCd(),
                successOrderList,
                failOrderList
        );
    }

    // 실패 주문 처리 (공통 로직)
    private FailOrderRequest handleFailedOrder(ReleaseOrder order, String reason) {
        return new FailOrderRequest(order.getOutOrdSlipNo(), reason);
    }

    // 성공 주문 처리 (공통 로직)
    private SuccessOrderRequest handleSuccessOrder(ReleaseOrder order, List<OrderLineVo> orderLineVoList) {
        return new SuccessOrderRequest(
                order.getOrderNo(),
                order.getOrderDt(),
                order.getOutOrdSlipNo(),
                order.getCstCd(),
                releaseMapper.toOrderLineList(orderLineVoList)
        );
    }

    // ZONE 기준 분리 처리
    private List<OrderLineVo> processZoneConfig(ConfigDetail configDetail, List<OrderLineVo> orderLineVoList) {
        List<Set<String>> zoneConfigSetList = toZoneConfigSetList(configDetail.getConfigContents().getZone());
        List<OrderLineVo>  calculateOrderLineVoList = calculateElementGod(zoneConfigSetList, orderLineVoList);
        return splitZoneConfig(zoneConfigSetList, calculateOrderLineVoList);
    }

    // 존 config를 Set으로 변환
    private List<Set<String>> toZoneConfigSetList(List<ZoneConfig> zoneConfigList) {
        List<Set<String>> zoneList = new ArrayList<>();
        for (ZoneConfig zoneConfig : zoneConfigList) {
            Set<String> set = new HashSet<>(zoneConfig.getZoneList());
            zoneList.add(set);
        }
        return zoneList;
    }

    // ZONE 기준 분리 로직
    private List<OrderLineVo> splitZoneConfig(List<Set<String>> zoneConfigSetList, List<OrderLineVo> orderLineVoList) {
        List<OrderLineVo> splitOrderList = new ArrayList<>();

        for (OrderLineVo orderLineVo : orderLineVoList) {
            List<OrderGodVo> orderGodVoList = orderLineVo.godList();
            List<OrderGodVo> remainingOrderGodVoList = new ArrayList<>(orderGodVoList); // 원본 복사

            for (Set<String> zoneConfigSet : zoneConfigSetList) {
                List<OrderGodVo> matchedOrderGodVoList = remainingOrderGodVoList.stream()
                        .filter(orderGodVo ->
                                isElementGod(orderGodVo)
                                        ? orderGodVo.elementGodLines().stream() // 모음 상품이면 모음 상품의 구성품들의 zone이 zone config와 모두 일치하는 것들만 리스트에 담음
                                            .allMatch(elementGodVo -> zoneConfigSet.contains(elementGodVo.zone()))
                                        : zoneConfigSet.contains(orderGodVo.zone())) // 단품 상품이면 상품의 zone이 zone config와 일치하는 것만 리스트에 담음
                        .collect(Collectors.toList());

                if (!matchedOrderGodVoList.isEmpty()) {
                    // zone config와 일치한 상품들은 order line으로 변환하여 리스트에 담음
                    splitOrderList.add(new OrderLineVo(matchedOrderGodVoList));

                    // 이미 zone config에 일치한 주문은 또 체크할 필요가 없어서 확인할 리스트에서 제거 함
                    remainingOrderGodVoList.removeAll(matchedOrderGodVoList);
                }
            }

            // zone config를 모두 돌았는데, 해당하는 zone이 없는 경우
            if (!remainingOrderGodVoList.isEmpty()) {
                return Collections.emptyList();
            }
        }
        return splitOrderList;
    }

    // 모음상품의 구성품이 모두 같은 zone config에 존재하는 경우 나누지 않아야 함
    private List<OrderLineVo> calculateElementGod(List<Set<String>> zoneConfigSetList, List<OrderLineVo> orderLineVoList) {
        List<OrderLineVo> splitOrderList = new ArrayList<>();

        for (OrderLineVo orderLineVo : orderLineVoList) {
            List<OrderGodVo> orderGodVoList = orderLineVo.godList();
            List<OrderGodVo> remainingOrderGodVoList = new ArrayList<>(orderGodVoList);

            List<OrderGodVo> elementOrdList = new ArrayList<>(); // 같은 존에 위치한 모음상품 목록

            // 모음 상품 체크
            for (Set<String> zoneConfigSet : zoneConfigSetList) {
                remainingOrderGodVoList.removeIf(orderGodVo -> {
                    if (isElementGod(orderGodVo)) {
                        // 모음상픔인데 모음상품의 모든 구성상품이 같은 zone config에 있는 경우 확인
                        boolean isSameZone = orderGodVo.elementGodLines()
                                .stream()
                                .allMatch(elementGod -> zoneConfigSet.contains(elementGod.zone()));

                        if (isSameZone) {
                            // 모음상품이 모두 같은 zone config에 존재하는 경우 라스트에 저장
                            elementOrdList.add(orderGodVo);
                            return true;
                        }
                    }
                    // 단품이거나 모음이다 다른 zone config에 있는 경우 false 리턴
                    return false;
                });
            }

            // 새로운 OrderGodVo 리스트 생성
            List<OrderGodVo> orderGodList = orderGodVoList.stream()
                    .flatMap(orderGodVo -> {
                        // 모음상품인데 다른 zone config에 있는 경우 모음상품의 구성품으로 orderGodVoList에 저장
                        if (isElementGod(orderGodVo) && !elementOrdList.contains(orderGodVo)) {
                            return orderGodVo.elementGodLines().stream()
                                    .map(elementGod -> new OrderGodVo(
                                            elementGod.godCd(),
                                            elementGod.ordQty() * orderGodVo.ordQty(), // 구성품 수량 계산
                                            elementGod.zone(),
                                            elementGod.completedBoxYn(),
                                            elementGod.lotNo(),
                                            elementGod.distTermDt(),
                                            elementGod.godType(),
                                            elementGod.elementGodLines()
                                    ));
                        } else {
                            // 단품상품이거나 모음상품인데 같은 zone config에 있는 경우 상품 그대로 리스트에 저장
                            return Stream.of(orderGodVo);
                        }
                    })
                    .collect(Collectors.toList());

            splitOrderList.add(new OrderLineVo(orderGodList));
        }

        return splitOrderList;
    }

    // 단품 상품 여부 판단
    private boolean isElementGod(OrderGodVo orderGodVo) {
        return orderGodVo.godType().equals(GodType.ELEMENT_GOD.getCode());
    }

    // 완박스 입력설정인 경우 입력설정된 상품인지 확인
    private boolean isInCwbSet(OrderGodVo orderGodVo, Set<String> cwbSet) {
        return cwbSet.contains(orderGodVo.godCd());
    }

    // 완박스 - 입력 설정인경우 입력 설정된 완박스 코드를 set 만들기
    private Set<String> toCwbConfigSet(List<CwbDetail> cwbList) {
        return cwbList.stream()
                .map(CwbDetail::getGodCd)
                .collect(Collectors.toSet());
    }

    // 완박스 분리 처리
    private List<OrderLineVo> processCwbConfig(ConfigDetail configDetail, List<OrderLineVo> orderLineVoList) {
        CwbConfig cwbConfig = configDetail.getConfigContents().getCwb();
        if (SettingType.ALL.getType().equals(cwbConfig.getSettingType())) {
            return splitCwbAll(orderLineVoList); // 완박스 전체 분리
        } else {
            return splitCwbByCustom(orderLineVoList, cwbConfig); // 완박스 입력 분리
        }
    }

    // 완박스 - 전체 분리
    private List<OrderLineVo> splitCwbAll(List<OrderLineVo> orderLineVoList) {
        List<OrderLineVo> splitOrderList = new ArrayList<>();

        for (OrderLineVo orderLineVo : orderLineVoList) {
            List<OrderGodVo> orderGodVoList = expandElementGods(orderLineVo.godList());
            List<OrderGodVo> etcOrderGodList = new ArrayList<>();

            for (OrderGodVo orderGodVo : orderGodVoList) {
                if (!isElementGod(orderGodVo) && "Y".equals(orderGodVo.completedBoxYn())) {
                    // 단품 상품 완박스인 경우
                    splitOrderList.addAll(splitCwbGodVo(orderGodVo, orderGodVo.ordQty()));
                } else if (isElementGod(orderGodVo)) {
                    // 모음상품 완박스인 경우
                    processCwbElementGod(orderGodVo, splitOrderList, etcOrderGodList);
                }
                else {
                    etcOrderGodList.add(orderGodVo);
                }
            }
            if (!etcOrderGodList.isEmpty()) {
                splitOrderList.add(new OrderLineVo(etcOrderGodList));
            }
        }

        return splitOrderList;
    }

    // 완박스 입력 기준 분리
    private List<OrderLineVo> splitCwbByCustom(List<OrderLineVo> orderLineVoList, CwbConfig cwbConfig) {
        List<OrderLineVo> splitOrderList = new ArrayList<>();
        Set<String> cwbSet = toCwbConfigSet(cwbConfig.getCwbList());

        for (OrderLineVo orderLineVo : orderLineVoList) {
            List<OrderGodVo> etcOrderGodList = new ArrayList<>();

            for (OrderGodVo orderGodVo : orderLineVo.godList()) {
                if (!isElementGod(orderGodVo) && "Y".equals(orderGodVo.completedBoxYn())) {
                    // 단품 상품 완박스인 경우
                    processCwbSingleGod(orderGodVo, cwbSet, splitOrderList, etcOrderGodList);
                } else if (isElementGod(orderGodVo)) {
                    // 모음상품 완박스인 경우
                    processCwbElementGod(orderGodVo, cwbSet, splitOrderList, etcOrderGodList);
                } else {
                    // 완박스 관리 대싱이 아닌 경우
                    etcOrderGodList.add(orderGodVo);
                }
            }
            if (!etcOrderGodList.isEmpty()) {
                splitOrderList.add(new OrderLineVo(etcOrderGodList));
            }
        }

        return splitOrderList;
    }

    // 완박스 단품인 경우 분리
    private void processCwbSingleGod(OrderGodVo orderGodVo, Set<String> cwbSet,
                                  List<OrderLineVo> splitOrderList, List<OrderGodVo> etcOrderGodList) {
        if (isInCwbSet(orderGodVo, cwbSet)) {
            splitOrderList.addAll(splitCwbGodVo(orderGodVo, orderGodVo.ordQty()));
        } else {
            // 완박스 입력 설정이 아닌 상품끼리 묶어줌
            etcOrderGodList.add(orderGodVo);
        }
    }

    // 입력 설정 - 완박스 모음인 경우 분리
    private void processCwbElementGod(OrderGodVo orderGodVo, Set<String> cwbSet,
                                      List<OrderLineVo> splitOrderList, List<OrderGodVo> etcOrderGodList) {
        if (isInCwbSet(orderGodVo, cwbSet)) {
            boolean isAllCompleted = orderGodVo.elementGodLines().stream()
                    .allMatch(elementGod -> "Y".equals(elementGod.completedBoxYn()));

            if (isAllCompleted) { // 구성 상품 전체가 완박스면 분리
                splitOrderList.addAll(splitCwbGodVo(orderGodVo, orderGodVo.ordQty()));
            } else {  // 구성 상품 전체가 완박스가 아니면 분리하지 않음
                etcOrderGodList.add(orderGodVo);
            }
        } else {
            etcOrderGodList.add(orderGodVo);
        }
    }

    // 전체설정 - 완박스 모음인 경우 분리
    private void processCwbElementGod(OrderGodVo orderGodVo, List<OrderLineVo> splitOrderList, List<OrderGodVo> etcOrderGodList) {
        // 구성 상품 전체가 완박스인지 확인
        boolean isAllCompleted = orderGodVo.elementGodLines().stream()
                .allMatch(elementGod -> "Y".equals(elementGod.completedBoxYn()));

        if (isAllCompleted) { // 구성 상품 전체가 완박스면 분리
            splitOrderList.addAll(splitCwbGodVo(orderGodVo, orderGodVo.ordQty()));
        } else {  // 구성 상품 전체가 완박스가 아니면 분리하지 않음
            etcOrderGodList.add(orderGodVo);
        }
    }

    // 모음 상품인 경우 모음 상품의 구성품으로 상품 리스트 생성
    private List<OrderGodVo> expandElementGods(List<OrderGodVo> orderGodVoList) {
        List<OrderGodVo> expandedList = new ArrayList<>();

        for (OrderGodVo orderGodVo : orderGodVoList) {
            if (isElementGod(orderGodVo)) {
                for (OrderGodVo elementOrderVo : orderGodVo.elementGodLines()) {
                    expandedList.add(new OrderGodVo(
                            elementOrderVo.godCd(),
                            elementOrderVo.ordQty() * orderGodVo.ordQty(), // 구성품의 주문 수량은 모음상품의 주문수량 * 구성상품의 구성 수 로 구함
                            elementOrderVo.zone(),
                            elementOrderVo.completedBoxYn(),
                            elementOrderVo.lotNo(),
                            elementOrderVo.distTermDt(),
                            elementOrderVo.godType(),
                            elementOrderVo.elementGodLines()
                    ));
                }
            } else {
                expandedList.add(orderGodVo);
            }
        }
        return expandedList;
    }

    // 완박스로 분리된 주문 상품 정보 vo 생성
    private List<OrderLineVo> splitCwbGodVo(OrderGodVo orderGodVo, Integer ordQty) {
        return IntStream.range(0, ordQty)
                .mapToObj(i -> new OrderLineVo(List.of(new OrderGodVo(
                        orderGodVo.godCd(),
                        1, // 완박스 분리는 무조건 낱개로 쪼개서, ord qty가 1로 고정
                        orderGodVo.zone(),
                        orderGodVo.completedBoxYn(),
                        orderGodVo.lotNo(),
                        orderGodVo.distTermDt(),
                        orderGodVo.godType(),
                        orderGodVo.elementGodLines()
                ))))
                .collect(Collectors.toList());
    }

}
