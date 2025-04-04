package fassto.fms.dynamicorderapi.domain.salesorder.dto.connectivity;

import lombok.Getter;

import java.util.List;

@Getter
public class Order {

    private String orderNo;

    private String orderDt;

    private String outOrdSlipNo;

    private String cstCd;

    private List<OrderLine> lines;

}
