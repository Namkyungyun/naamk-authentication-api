package kr.co.naamk.naamkauthenticationapi.web.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import kr.co.naamk.naamkauthenticationapi.domain.type.PenaltyStatusType;
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
public class UserDto {
    private Long id;
    private String name;
    private String nickname;
    private String email;
    private String role; // 계정 상태
    private Boolean penalty;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy.MM.dd HH:mm:ss", timezone = "Asia/Seoul")
    private Timestamp createdAt;

    public String getPenaltyStatus() {
        return PenaltyStatusType.fromStatusValue(penalty).getStatusName();
    }

    public String getUserStatus() {
        return RoleType.fromRoleName(role).getRoleNameKo();
    }


    @Setter @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class SearchRequest {
        private String userStatus;
        private Boolean penaltyStatus;
        private OffsetDateTime startDate;
        private OffsetDateTime endDate;
        private String name;
        private String nickname;
        private String email;
    }

    @Setter @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class SearchOption {
        private List<Map<String, String>> userStatus;
        private List<Map<String, Object>> penaltyStatus;
    }

    @Setter @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class UserDetailResponse {
        private Long id;
        private String name;
        private String nickname;
        private String email;
        private String intro;
        private String role;
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy.MM.dd HH:mm:ss", timezone = "Asia/Seoul")
        private Timestamp createdAt;
        private String thumbSUrl;
        private Boolean penalty;

        public String getPenaltyStatus() {
            return PenaltyStatusType.fromStatusValue(penalty).getStatusName();
        }

        public String getUserStatus() {
            return RoleType.fromRoleName(role).getRoleNameKo();
        }

        public List< Map< String, Object > > getPenaltyStatusList() {
            SearchCommon searchCommon = new SearchCommon();
            return searchCommon.penaltyStatus;
        }

    }

}
