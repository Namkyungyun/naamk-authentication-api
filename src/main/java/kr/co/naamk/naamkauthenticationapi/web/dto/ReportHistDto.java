package kr.co.naamk.naamkauthenticationapi.web.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import kr.co.naamk.naamkauthenticationapi.domain.type.PenaltyStatusType;
import kr.co.naamk.naamkauthenticationapi.domain.type.ReportStatusType;
import lombok.*;

import java.sql.Timestamp;

@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReportHistDto {
    private Long rowNum;
    private Long id;
    private Long reportUserId;
    private Long reportedUserId;
    private Boolean report = null;  // 접수 상태 [is_active]
    private Boolean penalty = null; // 현재 패널티 상태 [is_active]
    private String reportCreatedBy;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy.MM.dd HH:mm:ss", timezone = "Asia/Seoul")
    private Timestamp reportCreatedAt;

    public String getPenaltyStatus() {
        return PenaltyStatusType.fromStatusValue(false, penalty).getStatusName();
    }
    public String getReportStatus() {
        return ReportStatusType.fromStatusValue(report).getStatusName();
    }
}
