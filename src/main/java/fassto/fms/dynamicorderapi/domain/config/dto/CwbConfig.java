package fassto.fms.dynamicorderapi.domain.config.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;

import java.util.List;

@Getter
public class CwbConfig {

    @NotBlank(message = "settingType 필드는 필수 입력입니다.")
    private String settingType;

    @Valid
    private List<CwbDetail> cwbList;
}
