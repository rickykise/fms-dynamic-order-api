package fassto.fms.dynamicorderapi.domain.salesorder.dto.connectivity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.util.List;

@Getter
public class OrderLine {

    private final String godCd;

    private final Integer ordQty;

    private final String zone;

    private final String completedBoxYn;

    private final String lotNo;

    private final String distTermDt;

    private final String godType;

    private final List<OrderLine> elementGodLines;

    @JsonCreator
    public OrderLine(
            @JsonProperty("godCd") String godCd,
            @JsonProperty("ordQty") Integer ordQty,
            @JsonProperty("zone") String zone,
            @JsonProperty("completedBoxYn") String completedBoxYn,
            @JsonProperty("lotNo") String lotNo,
            @JsonProperty("distTermDt") String distTermDt,
            @JsonProperty("godType") String godType,
            @JsonProperty("elementGodLines") List<OrderLine> elementGodLines
    ) {
        this.godCd = godCd;
        this.ordQty = ordQty;
        this.zone = zone;
        this.completedBoxYn = completedBoxYn;
        this.lotNo = lotNo;
        this.distTermDt = distTermDt;
        this.godType = godType;
        this.elementGodLines = elementGodLines;
    }

}
