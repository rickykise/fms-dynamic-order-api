package fassto.fms.dynamicorderapi.global.enums;

import lombok.Getter;

@Getter
public enum GodType {

    SINGLE_GOD("1", "단품 상품"),
    ELEMENT_GOD("2", "모음 상품");

    private final String code;
    private final String description;

    GodType(String code, String description) {
        this.code = code;
        this.description = description;
    }
}
