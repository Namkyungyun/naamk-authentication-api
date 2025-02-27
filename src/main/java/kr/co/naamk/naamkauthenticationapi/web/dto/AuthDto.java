package kr.co.naamk.naamkauthenticationapi.web.dto;

import lombok.*;

@Data
@Builder
public class AuthDto {

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
        private String refreshToken;
    }

    @Setter @Getter
    @AllArgsConstructor @NoArgsConstructor
    public static class RefreshRequest {
        private Integer userId;
        private String refreshToken;
    }

    @Data
    @Builder
    public static class RefreshResponse {
        private Integer userId;
        private String username;
        private String accessToken;
    }

}
