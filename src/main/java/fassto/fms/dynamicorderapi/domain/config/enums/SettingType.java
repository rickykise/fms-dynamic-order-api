package fassto.fms.dynamicorderapi.domain.config.enums;

import lombok.Getter;

@Getter
public enum SettingType {

    CUSTOM("CUSTOM", "입력설정"),
    ALL("ALL", "전체설정");

    private final String type;
    private final String description;

    SettingType(String type, String description) {
        this.type = type;
        this.description = description;
    }
}
