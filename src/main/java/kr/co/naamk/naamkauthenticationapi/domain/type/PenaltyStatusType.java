package kr.co.naamk.naamkauthenticationapi.domain.type;

import com.querydsl.core.types.dsl.BooleanPath;
import kr.co.naamk.naamkauthenticationapi.exception.ServiceException;
import kr.co.naamk.naamkauthenticationapi.exception.type.ServiceMessageType;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum PenaltyStatusType {
    ok( true, "정상" ), block( false, "차단" );

    private final Boolean statusValue;
    private final String statusName;

    // roleName을 기반으로 RoleType 반환 메서드
    public static PenaltyStatusType fromStatusValue( Boolean statusValue) {
        for ( PenaltyStatusType penalty : PenaltyStatusType.values()) {
            if (penalty.getStatusValue().equals(statusValue)) {
                return penalty;
            }
        }
        throw new ServiceException( ServiceMessageType.REQUEST_PARAM_ERROR, "No penalty with value: " + statusValue );
    }

}
