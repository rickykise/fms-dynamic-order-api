package fassto.fms.dynamicorderapi.domain.salesorder.dto.connectivity;

import lombok.Getter;

@Getter
public class FailOrderRequest {
    private final String outOrdSlipNo;
    private final String message;

    public FailOrderRequest(String outOrdSlipNo, String message) {
        this.outOrdSlipNo = outOrdSlipNo;
        this.message = message;
    }
}
