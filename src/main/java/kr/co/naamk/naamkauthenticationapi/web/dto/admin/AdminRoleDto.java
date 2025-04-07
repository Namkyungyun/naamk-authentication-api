package kr.co.naamk.naamkauthenticationapi.web.dto.admin;

import lombok.*;

import java.util.List;

@Data
@Builder
public class AdminRoleDto {

    private Integer id;
    private String name;
    private String desc;
    private Boolean isActive;

    @AllArgsConstructor @NoArgsConstructor
    @Setter @Getter
    @Builder
    public static class CreateRequest {
        private String name;
        private String desc;
        private Boolean isActive;
    }

    @AllArgsConstructor @NoArgsConstructor
    @Setter @Getter
    @Builder
    public static class UpdateRequest {
        private Integer id;
        private String desc;
        private Boolean isActive;
    }


    @AllArgsConstructor @NoArgsConstructor
    @Setter @Getter
    @Builder
    public static class AccessRequest {
        private Integer roleId;
        private List< AdminAuthDto.ActiveRequest> perms;
        private List< AdminAuthDto.ActiveRequest> menus;
    }


    @AllArgsConstructor @NoArgsConstructor
    @Setter @Getter
    @Builder
    public static class AccessResponse {
        private List<RolePermResponse> perms;
        private List<RoleMenuResponse> menus;
    }


    @AllArgsConstructor @NoArgsConstructor
    @Setter @Getter
    @Builder
    public static class RolePermResponse {
        private Integer id;
        private String permCd;
        private Boolean isActive;
    }

    @AllArgsConstructor @NoArgsConstructor
    @Setter @Getter
    @Builder
    public static class RoleMenuResponse {
        private Integer id;
        private String menuCd;
        private Boolean isActive;
    }






}
