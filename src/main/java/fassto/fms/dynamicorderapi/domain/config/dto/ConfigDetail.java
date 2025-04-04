package fassto.fms.dynamicorderapi.domain.config.dto;

import fassto.fms.dynamicorderapi.domain.config.validation.ConfigContentsNotNullIfType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@ConfigContentsNotNullIfType
public class ConfigDetail {

    @NotNull(message = "priority 필드는 필수 입력입니다.")
    private Integer priority;

    @NotNull(message = "configType 필드는 필수 입력입니다.")
    private String configType;

    @NotBlank(message = "useYn 필드는 필수 입력입니다.")
    private String useYn;

    @Valid
    private ConfigContents configContents;

}
