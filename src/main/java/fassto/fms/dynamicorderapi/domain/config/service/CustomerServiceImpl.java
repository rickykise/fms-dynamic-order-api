package fassto.fms.dynamicorderapi.domain.config.service;

import fassto.fms.dynamicorderapi.domain.config.dto.ConfigDetail;
import fassto.fms.dynamicorderapi.domain.config.dto.CustomerConfigData;
import fassto.fms.dynamicorderapi.domain.config.entity.CustomerConfig;
import fassto.fms.dynamicorderapi.domain.config.dto.CustomerInfo;
import fassto.fms.dynamicorderapi.domain.config.dto.WarehouseCustomerInfos;
import fassto.fms.dynamicorderapi.domain.config.repository.CustomerRepository;
import fassto.fms.dynamicorderapi.global.exception.BusinessException;
import static fassto.fms.dynamicorderapi.global.exception.ExceptionMessages.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService{

    private final CustomerRepository customerRepository;

    // 센터에 등록된 고객사 목록 조회
    @Override
    public WarehouseCustomerInfos getCustomerConfig(String whCd) {
        validateWarehouseCode(whCd);

        return Optional.of(customerRepository.findByWhCdOrderByCstCd(whCd))
                .filter(list -> !list.isEmpty())
                .map(list -> setWarehouseCustomerInfo(whCd, list))
                .orElseThrow(() -> new BusinessException(CENTER_CUSTOMER_NOT_FOUND));
    }

    // 고객사 Config 조회
    @Override
    public CustomerConfigData getCustomerDetailConfig(String whCd, String cstCd) {
        validateWarehouseCode(whCd);

        if (cstCd == null || cstCd.isBlank()) {
            throw new BusinessException(CUSTOMER_INFO_REQUIRED);
        }

        CustomerConfig customerConfig = customerRepository.findByWhCdAndCstCd(whCd, cstCd).orElse(new CustomerConfig());

        return new CustomerConfigData(customerConfig);
    }

    @Override
    public List<ConfigDetail> getCustomerDetailConfigUsed(String whCd, String cstCd) {
        return customerRepository.findByWhCdAndCstCd(whCd, cstCd)
                .map(CustomerConfig::getConfigs) // Optional<CustomerConfig> → Optional<List<ConfigDetail>>
                .orElse(Collections.emptyList()) // 값이 없으면 빈 리스트 반환
                .stream()
                .filter(configDetail -> "Y".equals(configDetail.getUseYn())) // NullPointerException 방지
                .sorted(Comparator.comparing(ConfigDetail::getPriority))
                .toList();
    }

    // 고객사 Config 저장
    @Override
    public CustomerConfigData saveCustomerDetailConfig(CustomerConfig customerConfig) {
        if (isConfigExists(customerConfig)) {
            throw new BusinessException(CUSTOMER_CONFIG_ALREADY_EXISTS);
        }

        return new CustomerConfigData(customerRepository.save(customerConfig));
    }

    // 고객사 Config 업데이트
    @Override
    public CustomerConfigData updateCustomerDetailConfig(CustomerConfig customerConfig) {
        return customerRepository.findByWhCdAndCstCd(customerConfig.getWhCd(), customerConfig.getCstCd())
                .map(existingConfig -> updateExistingConfig(existingConfig, customerConfig))
                .orElseThrow(() -> new BusinessException(CUSTOMER_CONFIG_NOT_FOUND));
    }

    // 고객사 Config 존재 여부 확인
    private boolean isConfigExists(CustomerConfig customerConfig) {
        return customerRepository.findByWhCdAndCstCd(
                customerConfig.getWhCd(), customerConfig.getCstCd()
        ).isPresent();
    }

    // 센터 코드 필수 확인
    private void validateWarehouseCode(String whCd) {
        if (whCd == null || whCd.isBlank()) {
            throw new BusinessException(CENTER_CODE_REQUIRED);
        }
    }

    // 고객사 config update
    private CustomerConfigData updateExistingConfig(CustomerConfig existingConfig, CustomerConfig newConfig) {
        CustomerConfig updatedConfig = new CustomerConfig(existingConfig.getId(), newConfig);
        return new CustomerConfigData(customerRepository.save(updatedConfig));
    }

    // 고객사 config 정보
    private WarehouseCustomerInfos setWarehouseCustomerInfo(String whCd, List<CustomerConfig> customerConfigList) {
        return WarehouseCustomerInfos.builder()
                .whCd(whCd)
                .cstCdList(getCustomerInfo(customerConfigList))
                .build();
    }

    private List<CustomerInfo> getCustomerInfo(List<CustomerConfig> customerConfigList) {
        return customerConfigList.stream()
                .map(config -> CustomerInfo.builder()
                        .cstCd(config.getCstCd())
                        .cstNm(config.getTags().getCstNm())
                        .build())
                .toList();
    }
}
