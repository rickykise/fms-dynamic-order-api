package fassto.fms.dynamicorderapi.domain.salesorder.dto.connectivity;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class ReleaseResultRequest {

    private String id;
    private String whCd;
    private List<SuccessOrderRequest> successOrderList;
    private List<FailOrderRequest> failOrderList;

}
