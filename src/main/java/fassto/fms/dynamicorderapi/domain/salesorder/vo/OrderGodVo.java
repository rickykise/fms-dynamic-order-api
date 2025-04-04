package fassto.fms.dynamicorderapi.domain.salesorder.vo;

import java.util.List;

public record OrderGodVo(
        String godCd,
        Integer ordQty,
        String zone,
        String completedBoxYn,
        String lotNo,
        String distTermDt,
        String godType,
        List<OrderGodVo> elementGodLines
) {
}
