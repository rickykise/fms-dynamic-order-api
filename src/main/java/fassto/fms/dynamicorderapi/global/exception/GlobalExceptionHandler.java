package fassto.fms.dynamicorderapi.global.exception;

import fassto.fms.dynamicorderapi.global.response.CommonResponse;
import fassto.fms.dynamicorderapi.global.response.ErrorCode;
import com.fasterxml.jackson.core.JsonProcessingException;
import fassto.fms.dynamicorderapi.global.service.SlackService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
@RequiredArgsConstructor
@Slf4j
public class GlobalExceptionHandler {

    private final SlackService slackService;

    // 예상하지 못한 시스템 예외(Global Exception) 처리
    @ResponseBody
    @ExceptionHandler({Exception.class})
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<CommonResponse<Object>> handleException(Exception exception) {
        log.error(exception.getMessage(), exception);
        return CommonResponse.fail(ErrorCode.COMMON_BAD_REQUEST.getMessage());
    }

    // 비즈니스 로직에서 발생할 수 있는 예외 처리
    @ResponseBody
    @ExceptionHandler({BusinessException.class})
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<CommonResponse<Object>> businessException(Exception exception) throws JsonProcessingException {
        log.error(exception.getMessage());
        return CommonResponse.success(exception.getMessage());
    }

    // 비즈니스 로직에서 발생할 수 있는 예외 처리 - Slack 알림
    @ResponseBody
    @ExceptionHandler({SlackNotificationException.class})
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<CommonResponse<Object>> slackNotificationException(SlackNotificationException exception) {
        log.error(exception.getMessage());
        slackService.sendErrorNotification(exception);
        return CommonResponse.success(exception.getMessage());
    }

    // 요청 데이터 검증 실패(Validation) 예외 처리
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<CommonResponse<List<CommonResponse.Error>>> handleValidationException(MethodArgumentNotValidException ex) {
        List<CommonResponse.Error> errors = CommonResponse.Error.of(ex.getBindingResult());

        log.error("Validation error: {}", errors.stream()
                .map(e -> e.getFieldName() + ": " + e.getMessage())
                .collect(Collectors.joining(", ")));

        return CommonResponse.fail(errors, ErrorCode.COMMON_INVALID_PARAMETER.getMessage());
    }

    // 인증 실패(Unauthorized) 예외 처리
    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<CommonResponse<Object>> handleMethodArgumentNotValidException(
            Exception exception
    ) {
        log.error(exception.getMessage(), exception);
        return CommonResponse.fail(ErrorCode.UNAUTHORIZED.getMessage());
    }
}
