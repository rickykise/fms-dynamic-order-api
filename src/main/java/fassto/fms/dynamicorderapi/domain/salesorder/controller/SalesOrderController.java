package fassto.fms.dynamicorderapi.domain.salesorder.controller;

import fassto.fms.dynamicorderapi.domain.salesorder.dto.fms.ReleaseRequest;
import fassto.fms.dynamicorderapi.global.response.CommonResponse;
import fassto.fms.dynamicorderapi.global.service.QueueService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/dynamic-order/release")
@RequiredArgsConstructor
public class SalesOrderController {

    private final QueueService queueService;

    @Operation(summary = "다이나믹 오더 주문 분리",
            description = "분리할 주문 목록을 받아서 Queue에 저장")
    @PostMapping
    public ResponseEntity<CommonResponse<String>> sendDynamicOrder(
            @RequestBody @Valid ReleaseRequest releaseRequest
    ) {
        return CommonResponse.success(queueService.sendReleaseOrderList(releaseRequest));
    }

}
