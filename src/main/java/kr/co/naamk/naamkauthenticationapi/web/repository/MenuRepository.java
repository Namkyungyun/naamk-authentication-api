package kr.co.naamk.naamkauthenticationapi.web.repository;

import kr.co.naamk.naamkauthenticationapi.domain.TbMenus;
import kr.co.naamk.naamkauthenticationapi.domain.TbRoles;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MenuRepository extends JpaRepository< TbMenus, Integer > {
    Optional<TbMenus> findByCode( String code );

    List<TbMenus> findAllByParentIdOrderByOrder( Integer parentId );
}
