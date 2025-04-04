package fassto.fms.dynamicorderapi.domain.config.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = ConfigContentsValidator.class)
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ConfigContentsNotNullIfType {
    String message() default "ConfigDetail의 useYn 값이 'Y(사용함)'인 경우 configContents는 필수 입력 항목입니다.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}