package fassto.fms.dynamicorderapi.domain.salesorder.vo;

import java.util.List;

public record OrderLineVo(
        List<OrderGodVo> godList
) {
}
