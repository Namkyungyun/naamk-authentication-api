package kr.co.naamk.naamkauthenticationapi.web.dto.admin;

import lombok.*;

import java.sql.Timestamp;
import java.util.List;

@Data
@Builder
public class AdminUserDto {
    private Integer id;
    private String username;
    private String name;
    private String email;
    private Timestamp expiredDate;
    private List<String > authorities;


    @AllArgsConstructor @NoArgsConstructor
    @Setter @Getter
    @Builder
    public static class CreateRequest {
        private String username;
        private String password;
        private String name;
        private String email;
        private List<Integer> roleIds;
    }

    @AllArgsConstructor @NoArgsConstructor
    @Setter @Getter
    @Builder
    public static class UpdateRequest {
        private Integer id;
        private String name;
        private String email;
        private Boolean isActive;
    }

    @AllArgsConstructor @NoArgsConstructor
    @Setter @Getter
    @Builder
    public static class AccessRequest {
        private Integer id;
        private List< AdminAuthDto.ActiveRequest> roles;
    }

    @AllArgsConstructor @NoArgsConstructor
    @Setter @Getter
    @Builder
    public static class AccessResponse {
        private Integer id;
        private String username;
        private List<UserRoleResponse> roles;
    }

    @AllArgsConstructor @NoArgsConstructor
    @Setter @Getter
    @Builder
    public static class UserRoleResponse {
        private Integer id;
        private String roleName;
        private Boolean isActive;
    }

    @AllArgsConstructor @NoArgsConstructor
    @Getter @Setter
    @Builder
    public static class PasswordRequest {
        private Integer id;
        private String password;
    }

    @Data
    @Builder
    public static class PasswordResponse {
        private Integer id;
        private Timestamp expiredAt;
    }

}
