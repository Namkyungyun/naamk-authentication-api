package kr.co.naamk.naamkauthenticationapi.web.repository.admin;

import kr.co.naamk.naamkauthenticationapi.domain.admin.TbAdminRoles;
import kr.co.naamk.naamkauthenticationapi.domain.admin.TbAdminRoleMenus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AdminRoleMenusRepository extends JpaRepository< TbAdminRoleMenus, Integer > {
    List< TbAdminRoleMenus > findByIsActiveTrue();
    List< TbAdminRoleMenus > findByRole( TbAdminRoles role );
}
