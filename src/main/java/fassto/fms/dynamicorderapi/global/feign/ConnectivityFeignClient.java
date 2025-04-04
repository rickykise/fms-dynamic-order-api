package fassto.fms.dynamicorderapi.global.feign;

import fassto.fms.dynamicorderapi.domain.salesorder.dto.connectivity.ReleaseResultRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "connectivity", url = "${fms.connectivity.api.url}")
public interface ConnectivityFeignClient {

    @PostMapping("/api/dynamic-order/v1/sales-order")
    void release(@RequestBody ReleaseResultRequest releaseResultRequest);

}
