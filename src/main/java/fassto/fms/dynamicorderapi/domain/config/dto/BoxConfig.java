package fassto.fms.dynamicorderapi.domain.config.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class BoxConfig {

    @NotBlank(message = "boxRcmndYn 필드는 필수 입력입니다.")
    private String boxRcmndYn;

    @NotEmpty(message = "boxList 값은 필수 입력입니다.")
    @Valid
    private List<BoxDetail> boxList;
}
