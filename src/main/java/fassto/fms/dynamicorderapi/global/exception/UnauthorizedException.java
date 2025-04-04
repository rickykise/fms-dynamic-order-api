package fassto.fms.dynamicorderapi.global.exception;

import lombok.Getter;

@Getter
public class UnauthorizedException extends RuntimeException {

    private final String message;
    private final String code;

    public UnauthorizedException(String message) {
        this.message = message;
        this.code = "UNAUTHORIZED";
    }
}
