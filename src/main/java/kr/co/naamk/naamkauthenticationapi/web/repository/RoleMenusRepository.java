package kr.co.naamk.naamkauthenticationapi.web.repository;

import kr.co.naamk.naamkauthenticationapi.domain.TbRoleMenus;
import kr.co.naamk.naamkauthenticationapi.domain.TbRolePerms;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoleMenusRepository extends JpaRepository< TbRoleMenus, Integer > {
    List<TbRoleMenus> findByIsActiveTrue();

}
