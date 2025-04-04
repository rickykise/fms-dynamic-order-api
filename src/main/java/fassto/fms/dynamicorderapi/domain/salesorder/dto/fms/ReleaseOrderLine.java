package fassto.fms.dynamicorderapi.domain.salesorder.dto.fms;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.util.List;

@Getter
public class ReleaseOrderLine {

    @NotNull(message = "godCd 필드는 필수 입력입니다.")
    private final String godCd;

    @NotNull(message = "ordQty 필드는 필수 입력입니다.")
    @Min(value = 1, message = "ordQty는 1이상 입력입니다.")
    private final Integer ordQty;

    @NotNull(message = "zone 필드는 필수 입력입니다.")
    private final String zone;

    @NotNull(message = "completedBoxYn 필드는 필수 입력입니다.")
    private final String completedBoxYn;

    @NotNull(message = "lotNo 필드는 필수 입력입니다.")
    private final String lotNo;

    @NotNull(message = "distTermDt 필드는 필수 입력입니다.")
    private final String distTermDt;

    @NotNull(message = "godType 필드는 필수 입력입니다.")
    private final String godType;

    @NotNull(message = "compositions 필드는 필수 입력입니다.")
    private final List<Composition> compositions;

    @NotNull(message = "elementGodLines 필드는 필수 입력입니다.")
    private final List<ReleaseOrderLine> elementGodLines;

    @JsonCreator
    public ReleaseOrderLine(
            @JsonProperty("godCd") String godCd,
            @JsonProperty("ordQty") Integer ordQty,
            @JsonProperty("zone") String zone,
            @JsonProperty("completedBoxYn") String completedBoxYn,
            @JsonProperty("lotNo") String lotNo,
            @JsonProperty("distTermDt") String distTermDt,
            @JsonProperty("godType") String godType,
            @JsonProperty("compositions") List<Composition> compositions,
            @JsonProperty("elementGodLines") List<ReleaseOrderLine> elementGodLines
    ) {
        this.godCd = godCd;
        this.ordQty = ordQty;
        this.zone = zone;
        this.completedBoxYn = completedBoxYn;
        this.lotNo = lotNo;
        this.distTermDt = distTermDt;
        this.godType = godType;
        this.compositions = compositions;
        this.elementGodLines = elementGodLines;
    }

}
