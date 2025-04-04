package fassto.fms.dynamicorderapi.global.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MemoryResponse {

    private Long totalMemory;
    private Long freeMemory;
    private Long maxMemory;

    public MemoryResponse() {
        this.totalMemory = Runtime.getRuntime().totalMemory();
        this.freeMemory = Runtime.getRuntime().freeMemory();
        this.maxMemory = Runtime.getRuntime().maxMemory();
    }
}
