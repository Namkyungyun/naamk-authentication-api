package kr.co.naamk.naamkauthenticationapi.web.service;

import kr.co.naamk.naamkauthenticationapi.domain.admin.TbAdminMenus;
import kr.co.naamk.naamkauthenticationapi.domain.admin.TbAdminRoleMenus;
import kr.co.naamk.naamkauthenticationapi.domain.admin.TbAdminRoles;
import kr.co.naamk.naamkauthenticationapi.exception.ServiceException;
import kr.co.naamk.naamkauthenticationapi.exception.type.ServiceMessageType;
import kr.co.naamk.naamkauthenticationapi.mapstruct.AdminMenuMapper;
import kr.co.naamk.naamkauthenticationapi.web.dto.AdminMenuDto;
import kr.co.naamk.naamkauthenticationapi.web.repository.AdminMenuRepository;
import kr.co.naamk.naamkauthenticationapi.web.repository.AdminRoleMenusRepository;
import kr.co.naamk.naamkauthenticationapi.web.repository.AdminRoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AdminMenuService {

    private final AdminMenuRepository adminMenuRepository;
    private final AdminRoleRepository adminRoleRepository;
    private final AdminRoleMenusRepository adminRoleMenusRepository;


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
