package kr.co.naamk.naamkauthenticationapi.web.dto;

import kr.co.naamk.naamkauthenticationapi.domain.type.RoleType;
import lombok.Builder;
import lombok.Data;

import java.sql.Timestamp;

@Data
@Builder
public class UserDto {
    private Long id;
    private String username;
    private String email;
    private String name;
    private String nickname;
    private String userStatus; // 계정 상태
    private String penaltyStatus;
    private Timestamp createdAt;

}
