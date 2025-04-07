package kr.co.naamk.naamkauthenticationapi.web.repository.admin;

import kr.co.naamk.naamkauthenticationapi.domain.admin.TbAdminRoles;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AdminRoleRepository extends JpaRepository< TbAdminRoles, Integer > {
    Optional< TbAdminRoles > findByName( String name );

    List< TbAdminRoles > findByIsActiveTrue();
    List< TbAdminRoles > findByIdInAndIsActiveTrue( List<Integer> ids );
}
