package kr.co.naamk.naamkauthenticationapi.web.dto.admin;

import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
@Builder
public class AdminMenuDto {
    private Integer id;
    private String code;
    private String name;
    private String desc;
    private Integer parentId;
    private Integer order;
    private Boolean isActive;
    private String url;


    @AllArgsConstructor @NoArgsConstructor
    @Getter @Setter
    @Builder
    public static class CreateRequest {
        private String code;
        private String name;
        private String desc;
        private Integer parentId;
        private String url;
    }


    @NoArgsConstructor
    @AllArgsConstructor
    @Getter @Setter
    public static class MenuTreeDto {
        private Integer id;
        private String name;
        private String url;
        private Integer parentId;
        private Integer order;
        private Integer level;
        private String path;
        private List<MenuTreeDto> submenus;
    }


    @AllArgsConstructor @NoArgsConstructor
    @Getter @Setter
    @Builder
    public static class UpdateRequest {
        private Integer id;
        private String name;
        private String desc;
        private Integer order;
        private Boolean isActive;
        private String url;
        private Integer parentId;
    }

}
