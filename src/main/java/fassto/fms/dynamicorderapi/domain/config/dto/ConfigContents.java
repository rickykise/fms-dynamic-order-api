package fassto.fms.dynamicorderapi.domain.config.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class ConfigContents {

    @JsonProperty(required = false)
    @Valid
    private CwbConfig cwb;

    @JsonProperty(required = false)
    @Valid
    private BoxConfig box;

    @JsonProperty(required = false)
    @Valid
    private List<ZoneConfig> zone;
}
