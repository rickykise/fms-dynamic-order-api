package fassto.fms.dynamicorderapi.global.exception;

import fassto.fms.dynamicorderapi.domain.salesorder.dto.connectivity.ReleaseResultRequest;
import fassto.fms.dynamicorderapi.domain.salesorder.dto.fms.ReleaseRequest;

public class SlackNotificationException extends RuntimeException {
    private String whCd;
    private String trackingId;

    public SlackNotificationException(ExceptionMessages message, ReleaseRequest releaseRequest) {
        super(message.getMessage());
        this.whCd = releaseRequest.getWhCd();
        this.trackingId = releaseRequest.getId();
    }

    public SlackNotificationException(ExceptionMessages message, ReleaseResultRequest releaseResultRequest) {
        super(message.getMessage());
        this.whCd = releaseResultRequest.getWhCd();
        this.trackingId = releaseResultRequest.getId();
    }

    public String getWhCd() {
        return whCd;
    }

    public String getTrackingId() {
        return trackingId;
    }
}
