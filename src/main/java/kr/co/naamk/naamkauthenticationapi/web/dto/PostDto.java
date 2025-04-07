package kr.co.naamk.naamkauthenticationapi.web.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import kr.co.naamk.naamkauthenticationapi.domain.type.PenaltyStatusType;
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
public class PostDto {
    private Long rowNum;
    private Long id;
    private String type;
    private String content;
    private String userName;
    private String channelName;
    private Boolean penalty;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy.MM.dd HH:mm:ss", timezone = "Asia/Seoul")
    private Timestamp createdAt;

    public String getPenaltyStatus() {
        return PenaltyStatusType.fromStatusValue(true, penalty).getStatusName();
    }



    @Setter @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class SearchRequest {
        private Boolean penaltyStatus;
        private OffsetDateTime startDate;
        private OffsetDateTime endDate;
        private String userName;
        private String channelName;
    }

    @Setter @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class SearchOption {
        private List<Map<String, Object>> penaltyStatus;
    }


    @Setter @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class PostDetailResponse {
        private Long postId;
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy.MM.dd HH:mm:ss", timezone = "Asia/Seoul")
        private Timestamp createdAt;
        private String content;
        private String postStatus;
        private String channelPenaltyStatus;
        private Boolean penalty;
        private String channelName;
        private String channelNickName;
        private String userName;
        private Long userId;
        private Long popScore;
        private Long likeCount;
        private Long replyCount;
        private List<String> thumbs;

        public String getPenaltyStatus() {
            return PenaltyStatusType.fromStatusValue(true, penalty).getStatusName();
        }

        public List< Map< String, Object > > getPenaltyStatusList() {
            SearchCommon searchCommon = new SearchCommon();
            return searchCommon.penaltyStatus;
        }

    }

}
