package fassto.fms.dynamicorderapi.global.service;

import fassto.fms.dynamicorderapi.global.exception.SlackNotificationException;
import fassto.fms.dynamicorderapi.global.feign.SlackFeignClient;
import fassto.fms.dynamicorderapi.global.request.SlackMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class SlackService {
    private final SlackFeignClient slackFeignClient;

    public void sendErrorNotification(SlackNotificationException e) {
        String message = String.format(
                        "센터 : %s" +
                        "\ntrackingId : %s" +
                        "\n```" +  // 코드블럭 시작
                        "\n%s" +
                        "\n```", // 코드블럭 끝
                e.getWhCd(),
                e.getTrackingId(),
                e.getMessage()
        );

        try {
            slackFeignClient.sendMessage(new SlackMessage(message));
            log.info("Slack 알림 전송 완료");
        } catch (Exception slackException) {
            log.error("Slack 알림 전송 실패: {}", slackException.getMessage());
        }
    }
}
