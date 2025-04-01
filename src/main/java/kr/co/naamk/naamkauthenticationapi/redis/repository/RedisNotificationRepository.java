package kr.co.naamk.naamkauthenticationapi.redis.repository;

import kr.co.naamk.naamkauthenticationapi.redis.model.RedisNotificationEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RedisNotificationRepository extends CrudRepository< RedisNotificationEntity, String > {
    List<RedisNotificationEntity> findByUsername( String username);

}
