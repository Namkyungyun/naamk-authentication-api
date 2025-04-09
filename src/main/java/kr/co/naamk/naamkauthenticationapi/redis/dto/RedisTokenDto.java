package kr.co.naamk.naamkauthenticationapi.redis.dto;

import lombok.*;

import java.util.List;

@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RedisTokenDto {
    private String username;
    private String name;
    private String loginAt;
    private List<String> roles;
}
