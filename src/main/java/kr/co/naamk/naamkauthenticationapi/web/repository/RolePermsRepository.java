package kr.co.naamk.naamkauthenticationapi.web.repository;

import kr.co.naamk.naamkauthenticationapi.domain.TbRolePerms;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RolePermsRepository extends JpaRepository< TbRolePerms, Integer > {
    List<TbRolePerms> findByIdIn( List<Integer> ids );
    List<TbRolePerms> findByIsActiveTrue();

}
