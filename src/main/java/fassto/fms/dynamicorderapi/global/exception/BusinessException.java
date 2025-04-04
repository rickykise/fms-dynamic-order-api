package fassto.fms.dynamicorderapi.global.exception;

public class BusinessException extends RuntimeException {

    public BusinessException(ExceptionMessages message) {
        super(message.getMessage());
    }
}
