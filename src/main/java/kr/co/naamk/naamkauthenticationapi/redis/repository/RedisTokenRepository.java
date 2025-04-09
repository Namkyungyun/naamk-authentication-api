package kr.co.naamk.naamkauthenticationapi.redis.repository;

import kr.co.naamk.naamkauthenticationapi.redis.model.RedisTokenEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RedisTokenRepository extends CrudRepository< RedisTokenEntity, String > {
    RedisTokenEntity findByUsername( String username);
    Optional<RedisTokenEntity> findByAccessToken( String token);
}
