package fassto.fms.dynamicorderapi.domain.salesorder.dto.fms;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.util.List;

@Getter
public class ReleaseRequest {

    @NotNull(message = "id 필드는 필수 입력입니다.")
    private String id;

    @NotNull(message = "whCd 필드는 필수 입력입니다.")
    private String whCd;

    @NotEmpty(message = "orders 필드는 필수 입력입니다.")
    private List<ReleaseOrder> orders;

}
