package kr.co.naamk.naamkauthenticationapi.web.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import kr.co.naamk.naamkauthenticationapi.domain.type.PenaltyStatusType;
import lombok.*;

import java.sql.Timestamp;

@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PenaltyHistDto {
    private Long rowNum;
    private Long id;
    private String type;
    private Long linkedId;
    private Boolean penalty;
    private String description;
    private String createdBy;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy.MM.dd HH:mm:ss", timezone = "Asia/Seoul")
    private Timestamp createdAt;
    private Boolean isExistReport;

    public String getPenaltyStatus() {
        return PenaltyStatusType.fromStatusValue(true, penalty).getStatusName();
    }

    @Getter @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class CreateRequest {
        private Boolean isActive;
        private String description;
    }

    @Getter @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class CreateResponse {
        private String username;
        private Long linkedId;
        private String type;
        private Boolean isActive;
        private String description;
    }
}
