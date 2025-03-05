package kr.co.naamk.naamkauthenticationapi.web.service;

import kr.co.naamk.naamkauthenticationapi.domain.TbMenus;
import kr.co.naamk.naamkauthenticationapi.domain.TbRoleMenus;
import kr.co.naamk.naamkauthenticationapi.domain.TbRolePerms;
import kr.co.naamk.naamkauthenticationapi.domain.TbRoles;
import kr.co.naamk.naamkauthenticationapi.domain.type.Perms;
import kr.co.naamk.naamkauthenticationapi.exception.ServiceException;
import kr.co.naamk.naamkauthenticationapi.exception.type.ServiceMessageType;
import kr.co.naamk.naamkauthenticationapi.mapstruct.RoleMapper;
import kr.co.naamk.naamkauthenticationapi.web.dto.AuthDto;
import kr.co.naamk.naamkauthenticationapi.web.dto.RoleDto;
import kr.co.naamk.naamkauthenticationapi.web.repository.MenuRepository;
import kr.co.naamk.naamkauthenticationapi.web.repository.RoleMenusRepository;
import kr.co.naamk.naamkauthenticationapi.web.repository.RolePermsRepository;
import kr.co.naamk.naamkauthenticationapi.web.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
public class RoleService {

    private final RoleRepository roleRepository;
    private final MenuRepository menuRepository;
    private final RolePermsRepository rolePermsRepository;
    private final RoleMenusRepository roleMenusRepository;


    @Transactional
    public RoleDto createRole( RoleDto.CreateRequest dto ) {

        /// role
        Optional< TbRoles > role = roleRepository.findByName( dto.getName() );
        if ( role.isPresent() ) {
            throw new ServiceException( ServiceMessageType.ALREADY_EXIST, "The request name is already existing " );
        }

        TbRoles entity = RoleMapper.INSTANCE.createDtoToEntity( dto );
        TbRoles newRole = roleRepository.save( entity );

        /// rolePerms
        List< TbRolePerms > rolePerms = new ArrayList<>();
        List< Perms > perms = Arrays.stream( Perms.values() ).toList();
        for ( Perms perm : perms ) {
            TbRolePerms rolePerm = new TbRolePerms();
            rolePerm.setRole( newRole );
            rolePerm.setPermCd( perm.getCode() );
            rolePerm.setIsActive( false );

            rolePerms.add( rolePerm );
        }

        rolePermsRepository.saveAll( rolePerms );


        /// roleMenus
        List< TbRoleMenus > roleMenus = new ArrayList<>();
        List< TbMenus > menus = menuRepository.findAll();
        for ( TbMenus menu : menus ) {
            TbRoleMenus roleMenu = new TbRoleMenus();
            roleMenu.setRole( newRole );
            roleMenu.setMenu( menu );
            roleMenu.setIsActive( false );

            roleMenus.add( roleMenu );
        }
        roleMenusRepository.saveAll( roleMenus );

        return RoleMapper.INSTANCE.toDto( newRole );
    }


    @Transactional
    public RoleDto updateRole( RoleDto.UpdateRequest dto ) {
        TbRoles role = roleRepository.findById( dto.getId() )
                .orElseThrow( ( ) -> new ServiceException( ServiceMessageType.NOT_FOUND ) );

        role.setDesc( dto.getDesc() );
        role.setIsActive( dto.getIsActive() );

        TbRoles entity = roleRepository.save( role );

        return RoleMapper.INSTANCE.toDto( entity );
    }

    @Transactional
    public RoleDto.AccessResponse updateRoleAccess( RoleDto.AccessRequest dto ) {
        RoleDto.AccessResponse result = RoleDto.AccessResponse.builder().build();

        /// role check
        TbRoles role = roleRepository.findById( dto.getRoleId() )
                .orElseThrow( ( ) -> new ServiceException( ServiceMessageType.NOT_FOUND ) );

        /// request arr check
        List< AuthDto.ActiveRequest > requestPerms = dto.getPerms().stream().toList();
        List< AuthDto.ActiveRequest > requestMenus = dto.getMenus();

        if ( requestPerms.isEmpty() && requestMenus.isEmpty() ) {
            throw new ServiceException( ServiceMessageType.EMPTY_REQUEST, "all requested parameters are empty." );
        }

        /// perms
        if ( !requestPerms.isEmpty() ) {
            List< TbRolePerms> entities = rolePermsRepository.findByRole( role );
            List<Integer> ids = entities.stream().map( TbRolePerms::getId ).toList();

            for ( AuthDto.ActiveRequest request : requestPerms ) {
                boolean contains = ids.contains( request.getId() );
                if ( contains ) {
                    int index = ids.indexOf( request.getId() );
                    TbRolePerms tbRolePerms = entities.get( index );
                    tbRolePerms.setIsActive( request.getIsActive() );
                }
            }

            List< RoleDto.RolePermResponse > savedList = rolePermsRepository.saveAll( entities ).stream()
                    .map( el -> RoleDto.RolePermResponse.builder()
                            .id( el.getId() )
                            .permCd( el.getPermCd() )
                            .isActive( el.getIsActive() )
                            .build() )
                    .toList();

            result.setPerms( savedList );
        }

        /// menus
        if ( !requestMenus.isEmpty() ) {
            List< TbRoleMenus> entities = roleMenusRepository.findByRole( role );
            List<Integer> ids = entities.stream().map( TbRoleMenus::getId ).toList();

            for ( AuthDto.ActiveRequest request : requestMenus ) {
                boolean contains = ids.contains( request.getId() );
                if ( contains ) {
                    int index = ids.indexOf( request.getId() );
                    TbRoleMenus tbRoleMenus = entities.get( index );
                    tbRoleMenus.setIsActive( request.getIsActive() );
                }
            }

            List< RoleDto.RoleMenuResponse > savedList = roleMenusRepository.saveAll( entities ).stream()
                    .map( el -> RoleDto.RoleMenuResponse.builder()
                            .id( el.getId() )
                            .menuCd( el.getMenu().getCode() )
                            .isActive( el.getIsActive() )
                            .build()
                    ).toList();

           result.setMenus( savedList );
        }

        return result;
    }

}
