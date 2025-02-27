package kr.co.naamk.naamkauthenticationapi.config;

import io.lettuce.core.ReadFrom;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisPassword;
import org.springframework.data.redis.connection.RedisSentinelConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisKeyValueAdapter;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import org.springframework.data.redis.serializer.*;

/// Lettuce 클라이언트를 사용하여 Redis Sentinel과 연결하고, RedisTemplate을 설정하는 코드

@Configuration
@RequiredArgsConstructor
@EnableRedisRepositories(basePackages = {"kr.co.naamk.naamkauthenticationapi.redis.repository"}, enableKeyspaceEvents = RedisKeyValueAdapter.EnableKeyspaceEvents.ON_STARTUP)
public class RedisConfig {

    /// Redis 설정 정보를 담고 있는 `application.yml` 또는 `application.properties` 값
    private final RedisProperties redisProperties;

    @Bean
    /// Redis 데이터를 JSON 형태로 직렬화/역직렬화할 수 있도록 설정
    public RedisSerializer<Object> springSessionDefaultRedisSerializer() {
        return new GenericJackson2JsonRedisSerializer();
    }

    @Bean
    /// Redis Sentinel을 사용하여 Redis와 연결하는 `LettuceConnectionFactory` 설정
    protected LettuceConnectionFactory redisConnectionFactory(){

        // Lettuce 클라이언트 설정: `REPLICA_PREFERRED`를 사용하여 Slave에서 읽기를 우선 수행
        LettuceClientConfiguration clientConfig = LettuceClientConfiguration.builder()
                .readFrom( ReadFrom.REPLICA_PREFERRED )
                .build();

        // Redis Sentinel 설정 (마스터 및 Sentinel 노드 정보 설정)
        RedisSentinelConfiguration sentinelConfig = new RedisSentinelConfiguration()
                .master( redisProperties.getSentinel().getMaster() );


        // Sentinel 노드 설정: application.yml에 설정된 Sentinel 목록을 기반으로 Sentinel 서버 추가
        redisProperties.getSentinel().getNodes()
                .forEach( s -> sentinelConfig.sentinel( s.split( ":" )[0], Integer.valueOf(s.split( ":" )[1]) ) );

        // Redis 인증 비밀번호 설정 (비밀번호가 있는 경우 적용)
        sentinelConfig.setPassword( RedisPassword.of(redisProperties.getPassword()) );

        // Redis Sentinel 및 Lettuce 클라이언트 설정을 기반으로 `LettuceConnectionFactory` 생성
        return new LettuceConnectionFactory(sentinelConfig, clientConfig);
    }

    @Bean
    /// RedisTemplate을 사용하여 Redis에 데이터를 저장하고 조회하는 객체 설정
    public RedisTemplate<String, Object> redisTemplate() {
        final RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();

        // Redis 키를 문자열로 직렬화 (가독성 향상)
        redisTemplate.setKeySerializer( new StringRedisSerializer() );
        redisTemplate.setValueSerializer( new StringRedisSerializer() );

        // Hash 키는 문자열로 변환하여 저장
        redisTemplate.setHashKeySerializer( new GenericToStringSerializer<>(Object.class) );
        // Hash 값은 JDK 직렬화 방식 사용
        redisTemplate.setHashValueSerializer( new JdkSerializationRedisSerializer() );

        // Redis 연결 팩토리 설정 (Sentinel을 기반으로 생성된 `LettuceConnectionFactory` 사용)
        redisTemplate.setConnectionFactory(redisConnectionFactory());

        return redisTemplate;
    }
}
