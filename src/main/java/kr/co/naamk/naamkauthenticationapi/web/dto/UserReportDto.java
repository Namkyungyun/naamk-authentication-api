package kr.co.naamk.naamkauthenticationapi.web.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import kr.co.naamk.naamkauthenticationapi.domain.type.PenaltyStatusType;
import kr.co.naamk.naamkauthenticationapi.domain.type.ReportStatusType;
import kr.co.naamk.naamkauthenticationapi.domain.type.RoleType;
import kr.co.naamk.naamkauthenticationapi.domain.type.SearchCommon;
import lombok.*;

import java.sql.Timestamp;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;

@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserReportDto {
    private Long rowNum;
    private Long id;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy.MM.dd HH:mm:ss", timezone = "Asia/Seoul")
    private Timestamp latestCreatedAt;
    private Long reportedUserId;
    private String reportedUserName;
    private Long reportCount;
    private Boolean report = null; // 계정 상태
    private Boolean penalty = null;
    private String penaltyCreatedBy = null;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy.MM.dd HH:mm:ss", timezone = "Asia/Seoul")
    private Timestamp penaltyCreatedAt;

    public String getPenaltyStatus() {
        return PenaltyStatusType.fromStatusValue(false, penalty).getStatusName();
    }

    public String getReportStatus() {
        return ReportStatusType.fromStatusValue(report).getStatusName();
    }


    @Setter @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class SearchOption {
        private List<Map<String, Object>> reportStatus;
        private List<Map<String, Object>> penaltyStatus;
    }

    @Setter @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class SearchRequest {
        private Boolean reportStatus;
        private Boolean penaltyStatus;
        private String reportedName;
        private String penaltyCreatedBy;
        private OffsetDateTime startDate;
        private OffsetDateTime endDate;
    }

 // report test용
    @Setter @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class CreateRequest {
        private Long userId;
        private Long linkedId;
        private String type = "user";
    }

    @Setter @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class UserDetailResponse {
        private Long id;
        private Long reportedUserId;
        private String reportedUserName;
        private String role; // 계정 상태 [user_role]
        private Boolean report = null;  // 접수 상태 [is_active]
        private Boolean penalty = null; // 현재 패널티 상태 [is_active]
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy.MM.dd HH:mm:ss", timezone = "Asia/Seoul")
        private Timestamp latestCreatedAt;
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy.MM.dd HH:mm:ss", timezone = "Asia/Seoul")
        private Timestamp penaltyCreatedAt;
        private String penaltyCreatedBy;
        private String penaltyDescription;
        private String reportedUserProfileUrl;

        public String getPenaltyStatus() {
            return PenaltyStatusType.fromStatusValue(false, penalty).getStatusName();
        }

        public String getUserStatus() {
            return RoleType.fromRoleName(role).getRoleNameKo();
        }

        public List< Map< String, Object > > getPenaltyStatusList() {
            SearchCommon searchCommon = new SearchCommon();
            return searchCommon.penaltyStatus;
        }
    }

    @Setter @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class ReportHistResponse {
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


}
