package kr.co.naamk.naamkauthenticationapi.web.dto.admin;

import lombok.*;

import java.sql.Timestamp;

@Data
@Builder
public class AdminAuthDto {

    @Setter @Getter
    @AllArgsConstructor @NoArgsConstructor
    public static class LoginRequest {
        private String username;
        private String password;
    }

    @Data
    @Builder
    public static class LoginResponse {
        private Integer userId;
        private String accessToken;
        private Timestamp expiredAt;
    }

    @AllArgsConstructor @NoArgsConstructor
    @Setter @Getter
    @Builder
    public static class ActiveRequest {
        private Integer id;
        private Boolean isActive;
    }

    @Setter @Getter
    @AllArgsConstructor @NoArgsConstructor
    public static class RefreshRequest {
        private Integer userId;
    }

    @Data
    @Builder
    public static class RefreshResponse {
        private Integer userId;
        private String username;
        private String accessToken;
    }

}
