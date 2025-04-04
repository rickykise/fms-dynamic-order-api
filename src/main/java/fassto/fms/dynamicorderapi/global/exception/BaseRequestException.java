package fassto.fms.dynamicorderapi.global.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class BaseRequestException extends RuntimeException {

    private final String message;
    private final HttpStatus status;

    public BaseRequestException(String message, HttpStatus status) {
        super(message);
        this.message = message;
        this.status = status;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
