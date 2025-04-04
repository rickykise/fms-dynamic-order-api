package fassto.fms.dynamicorderapi.global.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommonResponse<T> {

    @Getter
    @NoArgsConstructor
    public static class Error {
        private String fieldName;
        private String message;

        public Error(String fieldName, String message) {
            this.fieldName = fieldName;
            this.message = message;
        }

        public static List<Error> of(BindingResult bindingResult){
            return bindingResult.getAllErrors().stream()
                    .map(error -> new Error(
                                    ((FieldError) error).getField()
                                    , error.getDefaultMessage()
                            )
                    ).toList();
        }
    }

    public enum Result {
        SUCCESS, FAIL
    }

    private Result result;
    private String message;
    private T data;

    public static <T> ResponseEntity<CommonResponse<T>> success(T data, String message) {
        return ResponseEntity.ok(CommonResponse.<T>builder()
                .result(Result.SUCCESS)
                .message(message)
                .data(data)
                .build());
    }

    public static <T> ResponseEntity<CommonResponse<T>> success(String message) {
        return success(null, message);
    }

    public static <T> ResponseEntity<CommonResponse<T>> success(T data) {
        return success(data, null);
    }

    public static ResponseEntity<CommonResponse<Object>> fail(String message) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(CommonResponse.builder()
                        .result(Result.FAIL)
                        .message(message)
                        .build());
    }

    public static <T> ResponseEntity<CommonResponse<T>> fail(T data, String message) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(CommonResponse.<T>builder()
                        .result(Result.FAIL)
                        .message(message)
                        .data(data)
                        .build());
    }

    public CommonResponse(String message, T data) {
        this.result = Result.SUCCESS;
        this.message = message;
        this.data = data;
    }
}
