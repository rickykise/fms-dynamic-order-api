package fassto.fms.dynamicorderapi.domain.salesorder.service;

import fassto.fms.dynamicorderapi.domain.salesorder.dto.connectivity.ReleaseResultRequest;
import fassto.fms.dynamicorderapi.domain.salesorder.dto.fms.ReleaseRequest;

public interface SalesOrderService {

    ReleaseResultRequest release(ReleaseRequest request);

}
