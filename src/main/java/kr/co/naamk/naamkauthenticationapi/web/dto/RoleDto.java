package kr.co.naamk.naamkauthenticationapi.web.dto;

import lombok.*;

import java.util.List;

@Data
@Builder
public class RoleDto {

    private Integer id;
    private String name;
    private String desc;
    private boolean isActive;

    @AllArgsConstructor @NoArgsConstructor
    @Setter @Getter
    @Builder
    public static class CreateRequest {
        private String name;
        private String desc;
        private boolean isActive;
    }




    @AllArgsConstructor @NoArgsConstructor
    @Setter @Getter
    @Builder
    public static class AuthorityRequest {
        private List<ActiveRequest> perms;
        private List<ActiveRequest> menus;
    }


    @AllArgsConstructor @NoArgsConstructor
    @Setter @Getter
    @Builder
    public static class ActiveRequest {
        private Integer id;
        private Boolean isActive;
    }

}
