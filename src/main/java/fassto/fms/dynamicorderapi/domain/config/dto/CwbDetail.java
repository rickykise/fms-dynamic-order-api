package fassto.fms.dynamicorderapi.domain.config.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class CwbDetail {

    @NotBlank(message = "godCd 필드는 필수 입력입니다.")
    private String godCd;

    @NotBlank(message = "godType 필드는 필수 입력입니다.")
    private String godType;

}
