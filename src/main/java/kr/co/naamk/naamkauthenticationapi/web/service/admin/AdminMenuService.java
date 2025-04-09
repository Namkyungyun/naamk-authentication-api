package kr.co.naamk.naamkauthenticationapi.web.service.admin;

import kr.co.naamk.naamkauthenticationapi.domain.admin.*;
import kr.co.naamk.naamkauthenticationapi.exception.ServiceException;
import kr.co.naamk.naamkauthenticationapi.exception.type.ServiceMessageType;
import kr.co.naamk.naamkauthenticationapi.mapstruct.admin.AdminMenuMapper;
import kr.co.naamk.naamkauthenticationapi.redis.model.RedisRoleEntity;
import kr.co.naamk.naamkauthenticationapi.redis.repository.RedisRoleRepository;
import kr.co.naamk.naamkauthenticationapi.utils.SecurityUtil;
import kr.co.naamk.naamkauthenticationapi.web.dto.admin.AdminMenuDto;
import kr.co.naamk.naamkauthenticationapi.web.repository.admin.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

@Service
@RequiredArgsConstructor
public class AdminMenuService {

    private final SecurityUtil securityUtil;
    private final AdminUserRepository adminUserRepository;
    private final AdminMenuRepository adminMenuRepository;
    private final AdminRoleRepository adminRoleRepository;
    private final AdminRoleMenusRepository adminRoleMenusRepository;

    private final RedisRoleRepository redisRoleRepository;

    @Transactional(readOnly = true)
    public List< AdminMenuDto.MenuTreeDto > getDisplayTreeByUserId() {

        /// 레디스에 저장된 전체 권한 정보 ( role 기반의 permissions(crud), menus )
        /// roleName, perms, menus
        List<RedisRoleEntity> redisRoleAccess = StreamSupport
                .stream(redisRoleRepository.findAll().spliterator(), false)
                .toList();

        /// 해당 유저의 모든 role 조회
        List< String > userRoleNames = securityUtil.getCurrentUserRoles();


        /// redisRoleAccess
        List<RedisRoleEntity> userRoleAccess = redisRoleAccess.stream()
                .filter( el -> userRoleNames.contains( el.getRoleName() ) )
                .toList();

        /// 접근 가능 메뉴추츨하기
        List<String> accessMenus = userRoleAccess.stream()
                .flatMap(el ->{ if(el.getMenus() != null) {
                    return el.getMenus().stream();
                }
                    return Stream.empty();
                })
                .distinct()
                .toList();


        List< Map< String, Object > > flatMenuList = adminMenuRepository.nativeFindMenuTreeByMenuCodes( accessMenus );

        // 2. MapStruct로 DTO 변환
        List< AdminMenuDto.MenuTreeDto > flatDtos = flatMenuList.stream()
                .map(AdminMenuMapper.INSTANCE::objToMenuTreeDTO)
                .collect(Collectors.toList());
        // 3. 트리 구조로 변환
        return buildMenuTree(flatDtos);
    }

    private List< AdminMenuDto.MenuTreeDto > buildMenuTree( List< AdminMenuDto.MenuTreeDto > flatList) {
        Map<Integer, AdminMenuDto.MenuTreeDto> menuMap = new HashMap<>();
        List<AdminMenuDto.MenuTreeDto> rootList = new ArrayList<>();

        // 먼저 map에 담기
        for (AdminMenuDto.MenuTreeDto node : flatList) {
            node.setSubmenus(new ArrayList<>()); // null 방지
            menuMap.put(node.getId(), node);
        }

        // 실제 트리 구성
        for (AdminMenuDto.MenuTreeDto node : flatList) {
            if (node.getParentId() == null) {
                rootList.add(node); // 루트 메뉴
            } else {
                AdminMenuDto.MenuTreeDto parent = menuMap.get(node.getParentId());
                if (parent != null) {
                    parent.getSubmenus().add(node); // 부모에 자식 추가
                }
            }
        }

        return rootList;
    }


    @Transactional
    public AdminMenuDto createMenu( AdminMenuDto.CreateRequest dto ) {
        /// checking menu
        Optional< TbAdminMenus > menu = adminMenuRepository.findByCode( dto.getCode() );
        if ( menu.isPresent() ) {
            throw new ServiceException( ServiceMessageType.ALREADY_EXIST, "The requested code is already exist" );
        }

        /// save menu
        int menusCnt = (int) adminMenuRepository.count();

        TbAdminMenus entity = AdminMenuMapper.INSTANCE.createDtoToEntity( dto );
        entity.setOrder( menusCnt + 1 );
        entity.setIsActive( false );
        entity.setUrl( dto.getUrl() );
        TbAdminMenus newMenu = adminMenuRepository.save( entity );


        /// save RoleMenu
        List< TbAdminRoleMenus > roleMenus = new ArrayList<>();
        List< TbAdminRoles > roles = adminRoleRepository.findAll();
        for ( TbAdminRoles role : roles ) {
            TbAdminRoleMenus roleMenu = new TbAdminRoleMenus();
            roleMenu.setRole( role );
            roleMenu.setMenu( newMenu );
            roleMenu.setIsActive( false );

            roleMenus.add( roleMenu );
        }

        adminRoleMenusRepository.saveAll( roleMenus );

        return AdminMenuMapper.INSTANCE.toDto( newMenu );
    }


    @Transactional
    public AdminMenuDto updateMenu( AdminMenuDto.UpdateRequest dto ) {
        /// checking menu
        TbAdminMenus entity = adminMenuRepository.findById( dto.getId() )
                .orElseThrow( ( ) -> new ServiceException( ServiceMessageType.NOT_FOUND, "The requested id is not exist" ) );

        List< TbAdminMenus > sameLevelList = adminMenuRepository.findAllByParentIdOrderByOrder( entity.getParentId() );
        if(dto.getOrder() > sameLevelList.size()) {
            dto.setOrder( sameLevelList.size() );
        }

        boolean isOrderReArrange = !entity.getOrder().equals( dto.getOrder() );


        /// update menus (consider re-order)
        entity.setName( dto.getName() );
        entity.setDesc( dto.getDesc() );
        entity.setOrder( dto.getOrder() );
        entity.setParentId( dto.getParentId() );
        entity.setIsActive( dto.getIsActive() );
        entity.setUrl( dto.getUrl() );


        /// 기존 메뉴 리스트에서 변경된 순서를 반영
        if ( isOrderReArrange ) {
            int orderNo = 1;
            sameLevelList.removeIf( el -> el.getId().equals( entity.getId() ) );
            sameLevelList.add(entity.getOrder()-1, entity);

            for( TbAdminMenus menu : sameLevelList) {
                menu.setOrder( orderNo );
                orderNo++;
            }

            adminMenuRepository.saveAll( sameLevelList );

        } else {
            adminMenuRepository.save( entity );
        }

        return AdminMenuMapper.INSTANCE.toDto( entity );
    }


    /**
     * delete ( delete는 되도록 사용 x )
     *
     * @param id
     * @return
     */
    @Transactional
    public Map<String, Boolean> deleteMenu( Integer id ) {

        /// checking menu
        TbAdminMenus entity = adminMenuRepository.findById( id )
                .orElseThrow( ( ) -> new ServiceException( ServiceMessageType.NOT_FOUND, "The request id does not exist" ) );


        /// update menus (consider re-order)
        List< TbAdminMenus > sameLevelList = adminMenuRepository.findAllByParentIdOrderByOrder( entity.getParentId() );
        sameLevelList.removeIf( el -> el.getId().equals( entity.getId() ) );

        int orderNo = 1;
        for( TbAdminMenus menu : sameLevelList) {
            menu.setOrder( orderNo );
            orderNo++;
        }

        adminMenuRepository.deleteById( id );
        adminMenuRepository.saveAll( sameLevelList );

        return Map.of("result", true);
    }


    @Transactional(readOnly = true)
    public List< AdminMenuDto > getActiveMenus( ) {
        return List.of();
    }

    @Transactional(readOnly = true)
    public List< AdminMenuDto > getAllMenus( ) {
        return List.of();
    }

    @Transactional(readOnly = true)
    public List< AdminMenuDto > getMenusByUserId( ) {
        return List.of();
    }

}
