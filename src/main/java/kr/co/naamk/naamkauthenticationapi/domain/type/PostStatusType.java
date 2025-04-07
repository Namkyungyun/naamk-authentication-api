package kr.co.naamk.naamkauthenticationapi.domain.type;

import kr.co.naamk.naamkauthenticationapi.exception.ServiceException;
import kr.co.naamk.naamkauthenticationapi.exception.type.ServiceMessageType;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum PostStatusType {
    active( true, "등록" ),
    inActive( false, "삭제" ),
    ;

    private final Boolean statusValue;
    private final String statusName;

    // roleName을 기반으로 RoleType 반환 메서드
    public static PostStatusType fromStatusValue(Boolean statusValue) {
        for ( PostStatusType postStatus : PostStatusType.values()) {
            if (postStatus.getStatusValue().equals(statusValue)) {
                return postStatus;
            }
        }
        throw new ServiceException( ServiceMessageType.REQUEST_PARAM_ERROR, "No postStatus with value: " + statusValue );
    }
}
