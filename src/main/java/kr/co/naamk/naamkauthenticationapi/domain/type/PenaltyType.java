package kr.co.naamk.naamkauthenticationapi.domain.type;

import kr.co.naamk.naamkauthenticationapi.exception.ServiceException;
import kr.co.naamk.naamkauthenticationapi.exception.type.ServiceMessageType;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum PenaltyType {
    post( "post" ), user( "user" );

    private final String penaltyName;

    // roleName을 기반으로 RoleType 반환 메서드
    public static PenaltyType fromPenaltyName( String penaltyName) {
        for ( PenaltyType penalty : PenaltyType.values()) {
            if (penalty.getPenaltyName().equalsIgnoreCase(penaltyName)) {
                return penalty;
            }
        }
        throw new ServiceException( ServiceMessageType.REQUEST_PARAM_ERROR, "No penalty with name: " + penaltyName );
    }

}
