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
        long menusCnt = menuRepository.count();

        TbMenus entity = MenuMapper.INSTANCE.createDtoToEntity( dto );
        entity.setOrder( (int) ( menusCnt ) + 1 );
        entity.setIsActive( false );
        TbMenus newMenu = menuRepository.save( entity );

        /// save RoleMenu
        List< TbRoleMenus > roleMenus = new ArrayList<>();
        List< TbRoles > roles = roleRepository.findAll();
        for ( TbRoles role : roles ) {
            TbRoleMenus roleMenu = new TbRoleMenus();
            roleMenu.setRole( role );
            roleMenu.setMenu( newMenu );

            roleMenus.add( roleMenu );
        }
        roleMenusRepository.saveAll( roleMenus );

        return MenuMapper.INSTANCE.toDto( newMenu );
    }


    @Transactional
    public Boolean updateMenu( Integer id, MenuDto.UpdateRequest dto ) {

        /// checking menu
        if ( !menuRepository.existsById( id ) ) {
            throw new ServiceException( ServiceMessageType.NOT_FOUND, "The request id does not exist" );
        }


        /// update menus (consider re-order)
        TbMenus entity = MenuMapper.INSTANCE.updateDtoToEntity( dto );
        entity.setId( id );

        /// 기존 메뉴 리스트에서 변경된 순서를 반영
        List< TbMenus > sameLevelList = menuRepository.findAllByParentIdOrderByOrder( dto.getParentId() );
        sameLevelList.stream()
                .filter( menu -> menu.getOrder() >= dto.getOrder() ) // 순서가 dto.getOrder() 이상인 메뉴들만 재정렬
                .forEach( menu -> menu.setOrder( menu.getOrder() + 1 ) );

        menuRepository.saveAll( sameLevelList );

        return true;
    }


    /**
     * delete ( delete는 되도록 사용 x )
     *
     * @param id
     * @return
     */
    @Transactional
    public Boolean deleteMenu( Integer id ) {

        /// checking menu
        TbMenus entity = menuRepository.findById( id )
                .orElseThrow( ( ) -> new ServiceException( ServiceMessageType.NOT_FOUND, "The request id does not exist" ) );


        /// update menus (consider re-order)
        List< TbMenus > sameLevelList = menuRepository.findAllByParentIdOrderByOrder( entity.getParentId() );
        sameLevelList.stream()
                .filter( menu -> menu.getOrder() >= entity.getOrder() ) // 순서가 dto.getOrder() 이상인 메뉴들만 재정렬
                .forEach( menu -> menu.setOrder( menu.getOrder() - 1 ) );
        menuRepository.saveAll( sameLevelList );


        /// delete
        menuRepository.deleteById( id );

        return true;
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
