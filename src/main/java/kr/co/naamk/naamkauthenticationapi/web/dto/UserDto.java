package kr.co.naamk.naamkauthenticationapi.web.dto;

import lombok.*;

import java.util.List;

@Data
@Builder
public class UserDto {

    @Data
    @AllArgsConstructor @NoArgsConstructor
    @Builder
    public static class CreateRequest {
        private String username;
        private String password;
        private String name;
        private String email;
        private List<Integer> roleIds;
    }

    @Setter @Getter
    @AllArgsConstructor @NoArgsConstructor
    @Builder
    public static class CreateResponse {
        private Integer userId;
        private String username;
        private List<String > authorities;
    }
}
