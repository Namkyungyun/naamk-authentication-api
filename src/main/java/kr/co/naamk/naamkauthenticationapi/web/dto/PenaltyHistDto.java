package kr.co.naamk.naamkauthenticationapi.web.dto;

import kr.co.naamk.naamkauthenticationapi.domain.type.PenaltyStatusType;
import lombok.*;

@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PenaltyHistDto {
    private Long id;
    private String type;
    private Long linkedId;
    private Boolean isActive;
    private String description;

    public String getPenaltyStatus() {
        return PenaltyStatusType.fromStatusValue(isActive).getStatusName();
    }
}
