package fassto.fms.dynamicorderapi.domain.config.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class ZoneConfig {

    @NotNull(message = "seq 필드는 필수 입력입니다.")
    private Integer seq;

    @NotEmpty(message = "zoneList 값은 필수 입력입니다.")
    private List<String> zoneList;
}

