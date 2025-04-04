package fassto.fms.dynamicorderapi.domain.config.service;

import fassto.fms.dynamicorderapi.domain.config.dto.ConfigDetail;
import fassto.fms.dynamicorderapi.domain.config.dto.CustomerConfigData;
import fassto.fms.dynamicorderapi.domain.config.entity.CustomerConfig;
import fassto.fms.dynamicorderapi.domain.config.dto.WarehouseCustomerInfos;

import java.util.List;

public interface CustomerService {
    WarehouseCustomerInfos getCustomerConfig(String whCd);
    CustomerConfigData getCustomerDetailConfig(String whCd, String cstCd);

    List<ConfigDetail> getCustomerDetailConfigUsed(String whCd, String cstCd);
    CustomerConfigData saveCustomerDetailConfig(CustomerConfig customerConfig);
    CustomerConfigData updateCustomerDetailConfig(CustomerConfig customerConfig);
}
