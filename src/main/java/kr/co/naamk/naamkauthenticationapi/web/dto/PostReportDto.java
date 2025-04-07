package kr.co.naamk.naamkauthenticationapi.web.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import kr.co.naamk.naamkauthenticationapi.domain.type.*;
import lombok.*;

import java.sql.Timestamp;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;

@Data
@Builder
public class PostReportDto {

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
        private String reportedUserName;
        private String reportedChannelName;
        private String penaltyCreatedBy;
        private OffsetDateTime startDate;
        private OffsetDateTime endDate;
    }


    @Getter @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class ListResponse {
        private Long rowNum;
        private Long id;
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy.MM.dd HH:mm:ss", timezone = "Asia/Seoul")
        private Timestamp latestCreatedAt;
        private Long reportedPostId;
        private String reportedUserName;
        private String reportedChannelName;
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
    }


    @Setter @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class DetailResponse {
        private Long id;
        private Boolean report = null;  // 접수 상태 [is_active]
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy.MM.dd HH:mm:ss", timezone = "Asia/Seoul")
        private Timestamp latestCreatedAt;
        private Long reportedUserId;
        private String reportedUserName;
        private Long reportedChannelId;
        private String reportedChannelName;
        private Long reportedPostId;
        private String reportedPostContent;
        private Boolean reportedPostActive;
        private Boolean penalty = null; // 현재 패널티 상태 [is_active]
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy.MM.dd HH:mm:ss", timezone = "Asia/Seoul")
        private Timestamp penaltyCreatedAt;
        private String penaltyCreatedBy;
        private String penaltyDescription;

        public String getReportedPostStatus() {return PostStatusType.fromStatusValue(reportedPostActive).getStatusName();}
        public String getPenaltyStatus() {
            return PenaltyStatusType.fromStatusValue(false, penalty).getStatusName();
        }
        public List< Map< String, Object > > getPenaltyStatusList() {
            SearchCommon searchCommon = new SearchCommon();
            return searchCommon.penaltyStatus;
        }
    }

    // report test용
    @Setter @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class CreateRequest {
        private Long userId;
        private Long linkedId;
        private String type;
        private Boolean isActive;
    }


}
