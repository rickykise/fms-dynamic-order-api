package fassto.fms.dynamicorderapi.domain.config.validation;

import fassto.fms.dynamicorderapi.domain.config.dto.ConfigContents;
import fassto.fms.dynamicorderapi.domain.config.dto.ConfigDetail;
import fassto.fms.dynamicorderapi.domain.config.enums.SettingType;
import fassto.fms.dynamicorderapi.global.enums.ConfigType;
import fassto.fms.dynamicorderapi.global.exception.ExceptionMessages;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Optional;

public class ConfigContentsValidator implements ConstraintValidator<ConfigContentsNotNullIfType, ConfigDetail> {

    @Override
    public boolean isValid(ConfigDetail configDetail, ConstraintValidatorContext context) {
        if (configDetail == null) {
            return true; // null 객체는 다른 검증에서 처리됨
        }

        String exceptionMessage = ExceptionMessages.CONFIG_CONTENTS_REQUIRED_FOR_BOX_ZONE.getMessage();
        String configTypeStr = configDetail.getConfigType();
        String useYn = configDetail.getUseYn();
        ConfigContents configContents = configDetail.getConfigContents();

        // ConfigType Enum 변환
        Optional<ConfigType> configTypeOpt = ConfigType.fromString(configTypeStr);

        if ("Y".equals(useYn) && configTypeOpt.isPresent()) {
            return validateConfigContents(configTypeOpt.get(), configContents, context, exceptionMessage);
        }

        return true;
    }

    private boolean validateConfigContents(ConfigType configType, ConfigContents configContents, ConstraintValidatorContext context, String exceptionMessage) {
        if (configContents == null) {
            addConfigContentsViolation(context, exceptionMessage);
            return false;
        }

        boolean isConfigDetailsMissing = switch (configType) {
            case CWB -> {
                if (configContents.getCwb() == null) {
                    yield true;
                }
                if (SettingType.CUSTOM.getType().equals(configContents.getCwb().getSettingType())
                        && configContents.getCwb().getCwbList().isEmpty()) {
                    exceptionMessage = ExceptionMessages.CONFIG_CONTENTS_REQUIRED_FOR_CWB.getMessage();
                    yield true;
                }
                yield false;
            }
            case BOX -> configContents.getBox() == null;
            case ZONE -> configContents.getZone() == null || configContents.getZone().isEmpty();
            default -> false;
        };

        if (isConfigDetailsMissing) {
            addConfigContentsViolation(context, exceptionMessage);
            return false;
        }

        return true;
    }

    private void addConfigContentsViolation(ConstraintValidatorContext context, String exceptionMessage) {
        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(exceptionMessage)
                .addPropertyNode("configContents")
                .addConstraintViolation();
    }
}
