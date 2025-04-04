package fassto.fms.dynamicorderapi.domain.salesorder.dto.connectivity;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class OrderLineRequest {

    private Integer seq;
    private List<OrderLine> godList;

}
