package fassto.fms.dynamicorderapi.domain.salesorder.mapper;

import fassto.fms.dynamicorderapi.domain.salesorder.dto.connectivity.OrderLine;
import fassto.fms.dynamicorderapi.domain.salesorder.dto.connectivity.OrderLineRequest;
import fassto.fms.dynamicorderapi.domain.salesorder.dto.fms.Composition;
import fassto.fms.dynamicorderapi.domain.salesorder.dto.fms.ReleaseOrderLine;
import fassto.fms.dynamicorderapi.domain.salesorder.vo.OrderGodVo;
import fassto.fms.dynamicorderapi.domain.salesorder.vo.OrderLineVo;
import fassto.fms.dynamicorderapi.global.enums.GodType;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class SalesOrderMapper {

    public List<OrderLineVo> toOrderLines(List<ReleaseOrderLine> orderLineRequests) {
        List<OrderGodVo> orderGodVoList = new ArrayList<>();

        for (ReleaseOrderLine releaseOrderLine : orderLineRequests) {
            if (releaseOrderLine.getGodType().equals("2")) {
                orderGodVoList.addAll(toOrderGodVoListForBundle(releaseOrderLine));
            } else {
                orderGodVoList.add(toOrderGodVo(releaseOrderLine));
            }
        }

        return Collections.singletonList(new OrderLineVo(orderGodVoList));
    }

    // 단품 상품 OrderGodVo로 변경
    private OrderGodVo toOrderGodVo(ReleaseOrderLine orderLine) {
        return new OrderGodVo(
                orderLine.getGodCd(),
                orderLine.getOrdQty(),
                orderLine.getZone(),
                orderLine.getCompletedBoxYn(),
                orderLine.getLotNo(),
                orderLine.getDistTermDt(),
                orderLine.getGodType(),
                null
        );
    }

    private List<OrderGodVo> toOrderGodVoList(List<ReleaseOrderLine> orderLineList) {
        return orderLineList.stream()
                .map(this::toOrderGodVo)
                .collect(Collectors.toList());
    }

    // 모음상품 OrderGodVo로 변경
    private List<OrderGodVo> toOrderGodVoListForBundle(ReleaseOrderLine bundleLine) {
        List<OrderGodVo> result = new ArrayList<>();

        // 1. 구성 상품, 구성 상품 정보로 MAP 구성
        Map<String, Queue<ReleaseOrderLine>> groupedElementLines = bundleLine.getElementGodLines()
                .stream()
                .collect(Collectors.groupingBy(
                        ReleaseOrderLine::getGodCd,
                        Collectors.collectingAndThen(Collectors.toList(), LinkedList::new)
                ));

        // 2. 구성 상품을 기준으로 묶인 리스트 생성
        for (int i = 0; i < bundleLine.getOrdQty(); i++) {
            List<OrderGodVo> compositionVoList = new ArrayList<>();

            for (Composition composition : bundleLine.getCompositions()) {
                String componentGodCd = composition.getGodCd();
                int requiredQty = composition.getOrdQty();

                List<ReleaseOrderLine> components = new ArrayList<>();
                int accumulatedQty = 0;

                Queue<ReleaseOrderLine> queue = groupedElementLines.get(componentGodCd);
                while (!queue.isEmpty() && accumulatedQty < requiredQty) {
                    ReleaseOrderLine current = queue.poll();

                    int remaining = requiredQty - accumulatedQty;
                    int qtyToTake = Math.min(current.getOrdQty(), remaining);

                    components.add(cloneWithNewQty(current, qtyToTake));
                    accumulatedQty += qtyToTake;

                    // 남은 수량 다시 큐에 넣기
                    int leftover = current.getOrdQty() - qtyToTake;
                    if (leftover > 0) {
                        queue.add(cloneWithNewQty(current, leftover));
                    }
                }

                // 구성 상품들을 OrderGodVo로 변환
                compositionVoList.addAll(toOrderGodVoList(components));
            }


            // 최종 모음 상품 하나 만들기
            result.add(new OrderGodVo(
                    bundleLine.getGodCd(),
                    1,
                    bundleLine.getZone(),
                    bundleLine.getCompletedBoxYn(),
                    bundleLine.getLotNo(),
                    bundleLine.getDistTermDt(),
                    bundleLine.getGodType(),
                    compositionVoList
            ));
        }

        return mergeSameBundle(result);
    }

    private ReleaseOrderLine cloneWithNewQty(ReleaseOrderLine origin, int newQty) {
        return new ReleaseOrderLine(
                origin.getGodCd(),
                newQty,
                origin.getZone(),
                origin.getCompletedBoxYn(),
                origin.getLotNo(),
                origin.getDistTermDt(),
                origin.getGodType(),
                origin.getCompositions(),
                origin.getElementGodLines()
        );
    }

    // 모음상품의 구성 상품 정복가 같은 경우 수량 합산
    private List<OrderGodVo> mergeSameBundle(List<OrderGodVo> generatedBundleList ) {
        Map<String, OrderGodVo> mergedMap = new LinkedHashMap<>();

        for (OrderGodVo vo : generatedBundleList) {
            // 구성품 정보 정렬해서 문자열로 만듦
            String elementKey = vo.elementGodLines() == null ? "" :
                    vo.elementGodLines().stream()
                            .sorted(Comparator
                                    .comparing(OrderGodVo::godCd)
                                    .thenComparing(OrderGodVo::zone)
                                    .thenComparing(OrderGodVo::lotNo)
                                    .thenComparing(OrderGodVo::ordQty))
                            .map(e -> e.godCd() + "_" + e.zone() + "_" + e.lotNo() + "_" + e.ordQty())
                            .collect(Collectors.joining("|"));

            // 전체 키 생성
            String key = vo.godCd() + "|" + vo.zone() + "|" + vo.lotNo() + "|" + elementKey;

            if (mergedMap.containsKey(key)) {
                OrderGodVo existing = mergedMap.get(key);
                // 수량 합치기
                mergedMap.put(key, new OrderGodVo(
                        existing.godCd(),
                        existing.ordQty() + vo.ordQty(),
                        existing.zone(),
                        existing.completedBoxYn(),
                        existing.lotNo(),
                        existing.distTermDt(),
                        existing.godType(),
                        existing.elementGodLines()
                ));
            } else {
                mergedMap.put(key, vo);
            }
        }

        return new ArrayList<>(mergedMap.values());
    }

    public List<OrderLineRequest> toOrderLineList (List<OrderLineVo> orderLineVoList) {
        List<OrderLineVo> orderLineVos = mergedSameGodLine(orderLineVoList);
        List<OrderLineRequest> successOrderResponses = new ArrayList<>();
        for (int i = 0; i < orderLineVoList.size(); i++) {
            successOrderResponses.add(
                new OrderLineRequest(i+1, toOrdeLineList(orderLineVos.get(i).godList()))
            );
        }
        return successOrderResponses;
    }

    // 모음 상품이 쪼개진 경우, 단품과 구성품이 다른 로우에 등장하는 경우 수량을 합쳐줌
    private List<OrderLineVo> mergedSameGodLine(List<OrderLineVo> orderLineVoList) {
        List<OrderLineVo> mergedOrderLine = new ArrayList<>();

        for (OrderLineVo orderLineVo : orderLineVoList) {
            Map<String, OrderGodVo> mergedMap = new HashMap<>();

            for (OrderGodVo godVo : orderLineVo.godList()) {
                // 오더 라인의 상품코드, 존, Lot가 같은 경우 같은 상품으로 판단하여 합침
                String key = godVo.godCd() + "_" + godVo.zone() + "_" + godVo.lotNo();

                mergedMap.merge(key, godVo, (existing, newGod) ->
                        new OrderGodVo(
                                existing.godCd(),
                                existing.ordQty() + newGod.ordQty(),  // ordQty 합산
                                existing.zone(),
                                existing.completedBoxYn(),
                                existing.lotNo(),
                                existing.distTermDt(),
                                existing.godType(),
                                existing.elementGodLines()
                        ));
            }
            mergedOrderLine.add(new OrderLineVo(new ArrayList<>(mergedMap.values())));
        }

        return mergedOrderLine;
    }

    private OrderLine toOrderLine(OrderGodVo orderGodVo) {
        List<OrderLine> elementGodLines = orderGodVo.godType().equals(GodType.SINGLE_GOD.getCode())
                ? Collections.emptyList()
                : toOrdeLineList(orderGodVo.elementGodLines());

        return new OrderLine(
                orderGodVo.godCd(),
                orderGodVo.ordQty(),
                orderGodVo.zone(),
                orderGodVo.completedBoxYn(),
                orderGodVo.lotNo(),
                orderGodVo.distTermDt(),
                orderGodVo.godType(),
                elementGodLines
        );
    }

    private List<OrderLine> toOrdeLineList(List<OrderGodVo> orderGodVoList) {
        return orderGodVoList.stream()
                .map(this::toOrderLine)
                .collect(Collectors.toList());
    }

}
