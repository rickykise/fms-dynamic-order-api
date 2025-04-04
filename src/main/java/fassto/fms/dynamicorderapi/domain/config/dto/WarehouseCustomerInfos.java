package fassto.fms.dynamicorderapi.domain.config.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class WarehouseCustomerInfos {

    private String whCd;
    private List<CustomerInfo> cstCdList;
}
