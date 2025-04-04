package fassto.fms.dynamicorderapi.domain.status.controller;

import fassto.fms.dynamicorderapi.global.response.CommonResponse;
import fassto.fms.dynamicorderapi.global.response.HealthResponse;
import fassto.fms.dynamicorderapi.global.response.MemoryResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/status")
@RequiredArgsConstructor
public class StatusApiController {

    @GetMapping("/health")
    public CommonResponse<HealthResponse> health() {
        return new CommonResponse<>("서버 상태", new HealthResponse());
    }

    @GetMapping("/mem-info")
    public CommonResponse<MemoryResponse> memInfo() {
        return new CommonResponse<>("서버 메모리", new MemoryResponse());
    }

}
