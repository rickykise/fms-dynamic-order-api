package fassto.fms.dynamicorderapi.global.service;

import fassto.fms.dynamicorderapi.domain.salesorder.dto.connectivity.ReleaseResultRequest;
import fassto.fms.dynamicorderapi.domain.salesorder.dto.fms.ReleaseRequest;

public interface QueueService {

    String sendReleaseOrderList(ReleaseRequest releaseRequest);

    void sendReTryOrderList(ReleaseResultRequest releaseResultRequest);

}
