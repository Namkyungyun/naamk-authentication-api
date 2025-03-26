package kr.co.naamk.naamkauthenticationapi.web.repository;

import kr.co.naamk.naamkauthenticationapi.domain.admin.TbAdminRolePerms;
import kr.co.naamk.naamkauthenticationapi.domain.admin.TbAdminRoles;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AdminRolePermsRepository extends JpaRepository< TbAdminRolePerms, Integer > {
    List< TbAdminRolePerms > findByIdIn( List<Integer> ids );
    List< TbAdminRolePerms > findByIsActiveTrue();
    List< TbAdminRolePerms > findByRole( TbAdminRoles role );

}
