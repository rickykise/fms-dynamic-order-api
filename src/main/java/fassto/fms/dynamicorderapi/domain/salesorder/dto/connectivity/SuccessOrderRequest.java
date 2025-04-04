package fassto.fms.dynamicorderapi.domain.salesorder.dto.connectivity;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class SuccessOrderRequest {

    private String orderNo;
    private String orderDt;
    private String outOrdSlipNo;
    private String cstCd;
    private List<OrderLineRequest> lines;

}
