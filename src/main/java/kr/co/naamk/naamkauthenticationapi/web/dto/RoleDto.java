package kr.co.naamk.naamkauthenticationapi.web.dto;

import lombok.*;

import java.util.List;

@Data
@Builder
public class RoleDto {

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
        private List<AuthDto.ActiveRequest> perms;
        private List<AuthDto.ActiveRequest> menus;
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
