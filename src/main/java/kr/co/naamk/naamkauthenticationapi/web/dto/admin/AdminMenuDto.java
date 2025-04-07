package kr.co.naamk.naamkauthenticationapi.web.dto.admin;

import lombok.*;

@Data
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
