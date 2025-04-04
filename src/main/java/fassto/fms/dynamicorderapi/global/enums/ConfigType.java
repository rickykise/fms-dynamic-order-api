package fassto.fms.dynamicorderapi.global.enums;

import lombok.Getter;

import java.util.Arrays;
import java.util.Optional;

@Getter
public enum ConfigType {

    CWB("CWB", "완박스"),
    BOX("BOX","박스 입수"),
    ZONE("ZONE", "존");

    private final String type;
    private final String description;

    ConfigType(String type, String description) {
        this.type = type;
        this.description = description;
    }

    public static Optional<ConfigType> fromString(String type) {
        return Arrays.stream(values())
                .filter(t -> t.type.equalsIgnoreCase(type))
                .findFirst();
    }
}
