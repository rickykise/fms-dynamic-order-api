package fassto.fms.dynamicorderapi.global.exception;

import lombok.Getter;

@Getter
public enum ExceptionMessages {

    /** CONFIG API  */
    CENTER_CUSTOMER_NOT_FOUND("센터에 대한 고객사 정보가 없습니다."),
    CUSTOMER_INFO_REQUIRED("고객사 정보는 필수 입력입니다."),
    CUSTOMER_CONFIG_ALREADY_EXISTS("이미 등록된 고객사 config 정보가 있습니다."),
    CUSTOMER_CONFIG_NOT_FOUND("고객사 config 정보가 없습니다."),
    CENTER_CODE_REQUIRED("센터 코드는 필수 입력입니다."),

    /** CONFIG TYPE  */
    CONFIG_CONTENTS_REQUIRED_FOR_BOX_ZONE("ConfigDetail의 useYn 값이 'Y(사용함)'인 경우 configContents는 필수 입력 항목입니다."),
    CONFIG_CONTENTS_REQUIRED_FOR_CWB("settingType 값이 'CUSTOM'인 경우 cwbList는 필수 입력 항목입니다."),

    /* RELEASE ORDER*/
    ORDER_PRODUCE_FAILED("다이나믹오더 주문 분리 객체 변환에 실패하였습니다."),
    KAFKA_L_FAILED("센터 코드는 필수 입력입니다.");

    private final String message;

    ExceptionMessages(String message) {
        this.message = message;
    }

    public static String message(ExceptionMessages exceptionMessage) {
        return exceptionMessage.getMessage();
    }
}
