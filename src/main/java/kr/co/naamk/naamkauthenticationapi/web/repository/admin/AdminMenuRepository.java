package kr.co.naamk.naamkauthenticationapi.web.repository.admin;

import kr.co.naamk.naamkauthenticationapi.domain.admin.TbAdminMenus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AdminMenuRepository extends JpaRepository< TbAdminMenus, Integer > {
    Optional< TbAdminMenus > findByCode( String code );

    List< TbAdminMenus > findAllByParentIdOrderByOrder( Integer parentId );
}
