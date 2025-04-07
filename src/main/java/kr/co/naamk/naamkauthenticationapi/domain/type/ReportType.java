package kr.co.naamk.naamkauthenticationapi.domain.type;

import kr.co.naamk.naamkauthenticationapi.exception.ServiceException;
import kr.co.naamk.naamkauthenticationapi.exception.type.ServiceMessageType;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ReportType {
    post( "post" ), user( "user" );

    private final String name;

    // roleName을 기반으로 RoleType 반환 메서드
    public static ReportType fromPenaltyName( String name) {
        for ( ReportType report : ReportType.values()) {
            if (report.getName().equalsIgnoreCase(name)) {
                return report;
            }
        }
        throw new ServiceException( ServiceMessageType.REQUEST_PARAM_ERROR, "No report with name: " + name );
    }

}
