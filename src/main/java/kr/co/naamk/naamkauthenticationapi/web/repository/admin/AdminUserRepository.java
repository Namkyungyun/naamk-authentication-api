package kr.co.naamk.naamkauthenticationapi.web.repository.admin;

import kr.co.naamk.naamkauthenticationapi.domain.admin.TbAdminUsers;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AdminUserRepository extends JpaRepository< TbAdminUsers, Integer> {
    Optional< TbAdminUsers > findByUsername( String userName );
}
