package kr.co.naamk.naamkauthenticationapi.web.repository.admin;

import kr.co.naamk.naamkauthenticationapi.domain.admin.TbAdminUserRoles;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AdminUserRolesRepository extends JpaRepository< TbAdminUserRoles, Integer> {
    List< TbAdminUserRoles > findByUserId( int userId );
}
