package kr.co.naamk.naamkauthenticationapi.redis.repository;

import kr.co.naamk.naamkauthenticationapi.redis.model.RedisRoleEntity;
import kr.co.naamk.naamkauthenticationapi.redis.model.RedisTokenEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RedisRoleRepository extends CrudRepository< RedisRoleEntity, String > {
    RedisRoleEntity findByRoleName( String roleName);
}
