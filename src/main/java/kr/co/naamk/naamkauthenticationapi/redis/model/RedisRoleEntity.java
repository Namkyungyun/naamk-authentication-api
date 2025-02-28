package kr.co.naamk.naamkauthenticationapi.redis.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Data
@EqualsAndHashCode(callSuper = false)
@Builder
@NoArgsConstructor
@AllArgsConstructor
@RedisHash(value="role")
public class RedisRoleEntity {
    @Id
    private String roleName;

    private List<String> perms;

    private List<String> menus;

    @TimeToLive(unit = TimeUnit.HOURS)
    private Long timeToLive; // 1일
}
