package fassto.fms.dynamicorderapi.domain.config.controller;

import fassto.fms.dynamicorderapi.domain.config.service.CustomerService;
import fassto.fms.dynamicorderapi.domain.config.dto.CustomerConfigData;
import fassto.fms.dynamicorderapi.domain.config.entity.CustomerConfig;
import fassto.fms.dynamicorderapi.domain.config.dto.WarehouseCustomerInfos;
import fassto.fms.dynamicorderapi.global.config.SwaggerConfig;
import fassto.fms.dynamicorderapi.global.response.CommonResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = SwaggerConfig.SwaggerTags.CONFIG)
@RestController
@RequestMapping("/api/v1/dynamic-order/config")
@RequiredArgsConstructor
public class CustomerConfigController {
    private final CustomerService customerService;

    @Operation(summary = "센터에 등록된 고객사 목록",
            description = "센터에 등록된 고객사 목록을 조회")
    @GetMapping
    public ResponseEntity<CommonResponse<WarehouseCustomerInfos>> getCustomerConfigs(
            @Parameter(description = "센터 코드") @RequestParam(required = true) String whCd
    ) {
        return CommonResponse.success(customerService.getCustomerConfig(whCd));
    }

    @Operation(summary = "고객사 config 정보",
            description = "고객사 config 정보 조회")
    @GetMapping("/{cstCd}")
    public ResponseEntity<CommonResponse<CustomerConfigData>> getCustomerDetailConfig(
            @Parameter(description = "센터 코드") @RequestParam(required = true) String whCd,
            @Parameter(description = "고객사 코드") @PathVariable String cstCd
    ) {
        return CommonResponse.success(customerService.getCustomerDetailConfig(whCd, cstCd));
    }

    @Operation(summary = "고객사 config 저장",
            description = "고객사 config 정보 저장")
    @PostMapping
    public ResponseEntity<CommonResponse<CustomerConfigData>> saveCustomerDetailConfig(
            @RequestBody @Valid CustomerConfigData customerConfigData
    ) {
        return CommonResponse.success(customerService.saveCustomerDetailConfig(new CustomerConfig(customerConfigData)));
    }

    @Operation(summary = "고객사 config 수정",
            description = "고객사 config 정보 수정")
    @PutMapping
    public ResponseEntity<CommonResponse<CustomerConfigData>> updateCustomerDetailConfig(
            @RequestBody @Valid CustomerConfigData customerConfigData
    ) {
        return CommonResponse.success(customerService.updateCustomerDetailConfig(new CustomerConfig(customerConfigData)));
    }
}
