package fassto.fms.dynamicorderapi.domain.config.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class BoxDetail {

    @NotBlank(message = "godCd 필드는 필수 입력입니다.")
    private String godCd;

    @NotBlank(message = "boxNo 필드는 필수 입력입니다.")
    private String boxNo;

    @NotNull(message = "boxInCnt 필드는 필수 입력입니다.")
    @Min(value = 1, message = "박스 개수는 1 이상이어야 합니다.")
    private Integer boxInCnt;

    @NotBlank(message = "godType 필드는 필수 입력입니다.")
    private String godType;

    @NotBlank(message = "splitYn 필드는 필수 입력입니다.")
    private String splitYn;

    @NotBlank(message = "configDiv 필드는 필수 입력입니다.")
    private String configDiv;
}

