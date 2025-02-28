package kr.co.naamk.naamkauthenticationapi.exception.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ServiceMessageType {
    SUCCESS(200, "SUCCESS"),
    NOT_DEFINED_ERROR( 9999, "This is an undefined ERROR." ),

    ERROR_SQL( 906, " SQL Error" ),
    ERROR_IO( 907, "Server IO Error" ),
    ERROR_DATA_CONVERT(908, "Data Convert Error"),
    ERROR_NULL_DATA(909, "Null value encountered during mapping"),


    REQUEST_PARAM_ERROR( 1303, "The requested parameter was invalid." ),
    REQUEST_ENTITY_TYPE_ERROR( 1304, "The requested Entity Type does not match." ),


    // authorization
    FAIL_LOGIN(4000, "로그인 실패"),
    FAIL_REFRESH(4006, "토큰 갱신 실패"),
    FAIL_CACHE_UPDATE(4007, "캐시 업데이트 실패"),

    SC_UNAUTHORIZED(4001, "클라이언트가 인증되지 않았기 때문에 요청을 정상적으로 처리할 수 없습니다."), // 인증 없이 api 요청한 경우
    SC_FORBIDDEN(4003, "클라이언트가 해당 요청에 대한 권한이 없습니다."), // 인증은 되었으나 권한이 없는 경우

    INVALID_TOKEN(4004, "Invalid JWT Token"),
    EXPIRED_TOKEN(4005, "Expired JWT Token"),

    NOT_FOUND(3000, "The requested resource could not be found."),
    ALREADY_EXIST(3001, "The requested resource already exist."),
    EMPTY_REQUEST(3002, "The requested values are empty."),
    ;

    private final Integer code;
    private final String serviceMessage;


    public static ServiceMessageType valueOfString(String messageString) {
        if (messageString == null) {
            return null;
        }
        for (ServiceMessageType type : ServiceMessageType.values()) {
            if (type.name().equalsIgnoreCase(messageString.trim())) {
                return type;
            }
        }
        return null; // 존재하지 않을 경우 null 반환
    }
}
