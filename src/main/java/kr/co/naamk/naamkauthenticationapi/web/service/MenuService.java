package kr.co.naamk.naamkauthenticationapi.web.service;

import kr.co.naamk.naamkauthenticationapi.domain.TbMenus;
import kr.co.naamk.naamkauthenticationapi.domain.TbRoleMenus;
import kr.co.naamk.naamkauthenticationapi.domain.TbRoles;
import kr.co.naamk.naamkauthenticationapi.exception.ServiceException;
import kr.co.naamk.naamkauthenticationapi.exception.type.ServiceMessageType;
import kr.co.naamk.naamkauthenticationapi.mapstruct.MenuMapper;
import kr.co.naamk.naamkauthenticationapi.web.dto.MenuDto;
import kr.co.naamk.naamkauthenticationapi.web.repository.MenuRepository;
import kr.co.naamk.naamkauthenticationapi.web.repository.RoleMenusRepository;
import kr.co.naamk.naamkauthenticationapi.web.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MenuService {

    private final MenuRepository menuRepository;
    private final RoleRepository roleRepository;
    private final RoleMenusRepository roleMenusRepository;


    @Transactional
    public MenuDto createMenu( MenuDto.CreateRequest dto ) {
        /// checking menu
        Optional< TbMenus > menu = menuRepository.findByCode( dto.getCode() );
        if ( menu.isPresent() ) {
            throw new ServiceException( ServiceMessageType.ALREADY_EXIST, "The requested code is already exist" );
        }

        /// save menu
        int menusCnt = (int) menuRepository.count();

        TbMenus entity = MenuMapper.INSTANCE.createDtoToEntity( dto );
        entity.setOrder( menusCnt + 1 );
        entity.setIsActive( false );
        entity.setUrl( dto.getUrl() );
        TbMenus newMenu = menuRepository.save( entity );


        /// save RoleMenu
        List< TbRoleMenus > roleMenus = new ArrayList<>();
        List< TbRoles > roles = roleRepository.findAll();
        for ( TbRoles role : roles ) {
            TbRoleMenus roleMenu = new TbRoleMenus();
            roleMenu.setRole( role );
            roleMenu.setMenu( newMenu );
            roleMenu.setIsActive( false );

            roleMenus.add( roleMenu );
        }

        roleMenusRepository.saveAll( roleMenus );

        return MenuMapper.INSTANCE.toDto( newMenu );
    }


    @Transactional
    public MenuDto updateMenu( MenuDto.UpdateRequest dto ) {
        /// checking menu
        TbMenus entity = menuRepository.findById( dto.getId() )
                .orElseThrow( ( ) -> new ServiceException( ServiceMessageType.NOT_FOUND, "The requested id is not exist" ) );

        List< TbMenus > sameLevelList = menuRepository.findAllByParentIdOrderByOrder( entity.getParentId() );
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

            for(TbMenus menu : sameLevelList) {
                menu.setOrder( orderNo );
                orderNo++;
            }

            menuRepository.saveAll( sameLevelList );

        } else {
            menuRepository.save( entity );
        }

        return MenuMapper.INSTANCE.toDto( entity );
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
        TbMenus entity = menuRepository.findById( id )
                .orElseThrow( ( ) -> new ServiceException( ServiceMessageType.NOT_FOUND, "The request id does not exist" ) );


        /// update menus (consider re-order)
        List< TbMenus > sameLevelList = menuRepository.findAllByParentIdOrderByOrder( entity.getParentId() );
        sameLevelList.removeIf( el -> el.getId().equals( entity.getId() ) );

        int orderNo = 1;
        for(TbMenus menu : sameLevelList) {
            menu.setOrder( orderNo );
            orderNo++;
        }

        menuRepository.deleteById( id );
        menuRepository.saveAll( sameLevelList );

        return Map.of("result", true);
    }


    @Transactional(readOnly = true)
    public List< MenuDto > getActiveMenus( ) {
        return List.of();
    }

    @Transactional(readOnly = true)
    public List< MenuDto > getAllMenus( ) {
        return List.of();
    }

    @Transactional(readOnly = true)
    public List< MenuDto > getMenusByUserId( ) {
        return List.of();
    }

}
