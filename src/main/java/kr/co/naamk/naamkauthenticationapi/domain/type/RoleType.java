package kr.co.naamk.naamkauthenticationapi.domain.type;

import kr.co.naamk.naamkauthenticationapi.exception.ServiceException;
import kr.co.naamk.naamkauthenticationapi.exception.type.ServiceMessageType;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum RoleType {
    provisional( "role_provisional", "?" ),
    user( "role_user", "정상" ),
    resetPwdUser( "role_reset", "PW 초기화" ),
    withdrawal( "role_withdraw", "탈퇴 신청" ),
    locked( "role_lock", "잠김" )
    ;

    private final String roleName;
    private final String roleNameKo;

    // roleName을 기반으로 RoleType 반환 메서드
    public static RoleType fromRoleName( String roleName ) {
        for ( RoleType role : RoleType.values() ) {
            if ( role.getRoleName().equalsIgnoreCase( roleName ) ) {
                return role;
            }
        }
        throw new ServiceException( ServiceMessageType.REQUEST_PARAM_ERROR, "No role with name: " + roleName );
    }

}
