package kr.co.naamk.naamkauthenticationapi.redis.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;
import org.springframework.data.redis.core.index.Indexed;

import java.io.Serializable;
import java.util.concurrent.TimeUnit;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@RedisHash(value = "NotificationRedis")
public class RedisNotificationEntity { // 정책 7일
    @Id
    private String id; // uuid

    @Indexed
    private String username; // 칼럼 username ( 암호화된 값 고대로 )

    private String message; //

    private String linkUrl; // user 혹은 post 제재 시, 해당 post혹은 user의 화면 연결 url

    private Integer popPower; // 제재 시 '0'

    private String createdAt; // dash없이 string으로

    private String type; // 카테고리 타입 -> 페널티는 penalty로 넣기

    @TimeToLive(unit = TimeUnit.HOURS)
    private Long timeToLive; // 정책 7일
}
