package kr.co.naamk.naamkauthenticationapi.domain.type;

import kr.co.naamk.naamkauthenticationapi.exception.ServiceException;
import kr.co.naamk.naamkauthenticationapi.exception.type.ServiceMessageType;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ReportStatusType {
    unComplete( true, "접수" ),
    complete( false, "처리완료" ),
;
    private final Boolean statusValue;
    private final String statusName;

    // roleName을 기반으로 RoleType 반환 메서드
    public static ReportStatusType fromStatusValue( Boolean statusValue) {
        for ( ReportStatusType report : ReportStatusType.values()) {
            if (report.getStatusValue().equals(statusValue)) {
                return report;
            }
        }
        throw new ServiceException( ServiceMessageType.REQUEST_PARAM_ERROR, "No report type with value: " + statusValue );
    }

}
