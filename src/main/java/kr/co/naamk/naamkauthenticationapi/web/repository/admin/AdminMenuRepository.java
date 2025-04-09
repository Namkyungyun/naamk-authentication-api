package kr.co.naamk.naamkauthenticationapi.web.repository.admin;

import kr.co.naamk.naamkauthenticationapi.domain.admin.TbAdminMenus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public interface AdminMenuRepository extends JpaRepository< TbAdminMenus, Integer > {
    Optional< TbAdminMenus > findByCode( String code );

    List< TbAdminMenus > findAllByParentIdOrderByOrder( Integer parentId );


    /** 전체 메뉴 트리 (검색x) && rootMenu 제외 */
    @Query(nativeQuery = true,
            value = """        
                    WITH RECURSIVE menu_tree AS (
                        -- 1. 최상위 메뉴
                        SELECT
                            id AS root_id,
                            id,
                            parent_id,
                            code,
                            name,
                            url,
                            "order",
                            0 AS level,
                            CAST(id AS TEXT) AS path
                        FROM admin.menus
                        WHERE parent_id IS NULL
                        AND code IN (:menuCodes) -- 조건 걸기
                        UNION ALL
                    
                        -- 2. 하위 메뉴들 (접근 가능한 코드만 포함)
                        SELECT
                            mt.root_id,
                            m.id,
                            m.parent_id,
                            m.code,
                            m.name,
                            m.url,
                            m."order",
                            mt.level + 1,
                            mt.path || ',' || m.id
                        FROM admin.menus m
                        INNER JOIN menu_tree mt ON m.parent_id = mt.id
                        WHERE m.code IN (:menuCodes) -- 조건 걸기
                    )
                    
                    -- 3. 최종 결과 반환
                    SELECT DISTINCT ON (id)
                        id,
                        parent_id as parentId,
                        name,
                        code,
                        url,
                        root_id as rootId,
                        level,
                        "order",
                        path
                    FROM menu_tree
                    ORDER BY id, root_id, level, "order"
                    """)
    List< Map<String, Object> > nativeFindMenuTreeByMenuCodes(@Param( "menuCodes" ) List<String> menuCodes);
}
