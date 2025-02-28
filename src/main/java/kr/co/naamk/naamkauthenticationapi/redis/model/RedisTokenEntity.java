package kr.co.naamk.naamkauthenticationapi.redis.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;
import org.springframework.data.redis.core.index.Indexed;

import java.util.concurrent.TimeUnit;

@Data
@EqualsAndHashCode(callSuper = false)
@Builder
@NoArgsConstructor
@AllArgsConstructor
@RedisHash(value="access_token")
public class RedisTokenEntity {
    @Id
    private String userId; // access_token:${userId}

    @Indexed
    private String accessToken;

    @TimeToLive(unit = TimeUnit.HOURS)
    private Long timeToLive; // 1일
}
