package fassto.fms.dynamicorderapi.domain.salesorder.dto.fms;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.util.List;

@Getter
public class ReleaseOrder {

    @NotNull(message = "orderNo 필드는 필수 입력입니다.")
    private String orderNo;

    @NotNull(message = "orderDt 필드는 필수 입력입니다.")
    private String orderDt;

    @NotNull(message = "outOrdSlipNo 필드는 필수 입력입니다.")
    private String outOrdSlipNo;

    @NotNull(message = "cstCd 필드는 필수 입력입니다.")
    private String cstCd;

    @NotEmpty(message = "lines 필드는 필수 입력입니다.")
    private List<ReleaseOrderLine> lines;

}
