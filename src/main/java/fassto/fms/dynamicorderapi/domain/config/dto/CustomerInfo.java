package fassto.fms.dynamicorderapi.domain.config.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class CustomerInfo {

    private String cstCd;
    private String cstNm;
}
