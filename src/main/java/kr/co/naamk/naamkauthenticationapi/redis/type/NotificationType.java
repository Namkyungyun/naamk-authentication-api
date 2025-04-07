package kr.co.naamk.naamkauthenticationapi.redis.type;

import kr.co.naamk.naamkauthenticationapi.domain.type.PenaltyType;
import kr.co.naamk.naamkauthenticationapi.exception.ServiceException;
import kr.co.naamk.naamkauthenticationapi.exception.type.ServiceMessageType;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum NotificationType {
    post("post"),
    user("user"),
//    channel("channel"),
    ;

    private final String type;


    // roleName을 기반으로 RoleType 반환 메서드
    public static NotificationType fromTypeName( String typeName) {
        for ( NotificationType notification : NotificationType.values()) {
            if (notification.getType().equalsIgnoreCase(typeName)) {
                return notification;
            }
        }
        throw new ServiceException( ServiceMessageType.REQUEST_PARAM_ERROR, "No notification with name: " + typeName );
    }
}
