package fassto.fms.dynamicorderapi.global.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import fassto.fms.dynamicorderapi.domain.salesorder.dto.connectivity.ReleaseResultRequest;
import fassto.fms.dynamicorderapi.domain.salesorder.dto.fms.ReleaseRequest;
import fassto.fms.dynamicorderapi.domain.salesorder.service.SalesOrderService;
import fassto.fms.dynamicorderapi.global.exception.BusinessException;
import fassto.fms.dynamicorderapi.global.exception.SlackNotificationException;
import fassto.fms.dynamicorderapi.global.feign.ConnectivityFeignClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import static fassto.fms.dynamicorderapi.global.exception.ExceptionMessages.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaServiceImpl implements QueueService {

    private final String DYNAMIC_ORDER_TOPIC = "fssmonitor_dynamicorder_order-release";
    private final String DYNAMIC_RETRY_ORDER_TOPIC = "dynamicorder_conncetivity_retry_order_release";

    private final SalesOrderService salesOrderService;
    private final ConnectivityFeignClient connectivityFeignClient;

    private final KafkaTemplate<String, Object> kafkaTemplate;
    ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String sendReleaseOrderList(ReleaseRequest releaseRequest) {
        try {
            String message = objectMapper.writeValueAsString(releaseRequest);
            kafkaTemplate.send(DYNAMIC_ORDER_TOPIC, message);
            return "다이나믹오더 주문 분리 객체 변환 성공";
        } catch (JsonProcessingException e) {
            log.error("다이나믹오더 kafka send 실패. (orderId: {}) 원본 메시지 : {}, 오류 내용 : {}", releaseRequest.getId(), releaseRequest, e.getMessage(), e);
            throw new SlackNotificationException(ORDER_PRODUCE_FAILED, releaseRequest);
        }
    }

    @Override
    public void sendReTryOrderList(ReleaseResultRequest releaseResultRequest) {
        try {
            String message = objectMapper.writeValueAsString(releaseResultRequest);
            kafkaTemplate.send(DYNAMIC_RETRY_ORDER_TOPIC, message);
        } catch (JsonProcessingException e) {
            log.error("다이나믹오더 kafka send 실패. (orderId: {}) 원본 메시지 : {}, 오류 내용 : {}", releaseResultRequest.getId(), releaseResultRequest, e.getMessage(), e);
            throw new BusinessException(ORDER_PRODUCE_FAILED);
        }
    }

    @KafkaListener(topics = DYNAMIC_ORDER_TOPIC, containerFactory = "releaseOrderKafkaListenerContainerFactory")
    public void releaseOrderListConsume(String message) {
        try {
            ReleaseRequest releaseRequest = objectMapper.readValue(message, ReleaseRequest.class);
            log.info("다이나믹 오더 대상 주문 (orderId: {}) : {}", releaseRequest.getId(), message);
            ReleaseResultRequest releaseResultRequest = salesOrderService.release(releaseRequest);
            callConnectivityApi(releaseResultRequest);
        } catch (JsonProcessingException e) {
            log.error("다이나믹오더 주문 분리 객체 변환 실패. 원본 메시지 : {}, 오류 내용 : {}", message, e.getMessage(), e);
        }
    }

    private void callConnectivityApi(ReleaseResultRequest releaseResultRequest) {
        try {
            connectivityFeignClient.release(releaseResultRequest);
        } catch (Exception e) {
            log.error("다이나믹 오더 connectivity 전송 실패 (orderId: {}) : {}, 오류 내용 : {}", releaseResultRequest.getId(), releaseResultRequest, e.getMessage(), e);
            sendReTryOrderList(releaseResultRequest);
        }
    }

}
