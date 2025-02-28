package kr.co.naamk.naamkauthenticationapi.web.service;

import kr.co.naamk.naamkauthenticationapi.domain.TbMenus;
import kr.co.naamk.naamkauthenticationapi.domain.TbRoleMenus;
import kr.co.naamk.naamkauthenticationapi.domain.TbRolePerms;
import kr.co.naamk.naamkauthenticationapi.domain.TbRoles;
import kr.co.naamk.naamkauthenticationapi.domain.type.Perms;
import kr.co.naamk.naamkauthenticationapi.exception.ServiceException;
import kr.co.naamk.naamkauthenticationapi.exception.type.ServiceMessageType;
import kr.co.naamk.naamkauthenticationapi.mapstruct.RoleMapper;
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
    public Boolean updateRoleAuthorities( RoleDto.AuthorityRequest dto ) {
        List< RoleDto.ActiveRequest > requestPerms = dto.getPerms();
        List< RoleDto.ActiveRequest > requestMenus = dto.getMenus();

        if ( requestPerms.isEmpty() && requestMenus.isEmpty() ) {
            throw new ServiceException( ServiceMessageType.EMPTY_REQUEST );
        }

        /// perms
        if ( !requestPerms.isEmpty() ) {
            List< TbRolePerms > permEntities = new ArrayList<>();

            for ( RoleDto.ActiveRequest requestPerm : requestPerms ) {
                String permErrorMsg = "A non-existent rolePermId exists. : " + requestPerm.getId();

                TbRolePerms entity = rolePermsRepository.findById( requestPerm.getId() )
                        .orElseThrow( ( ) -> new ServiceException( ServiceMessageType.NOT_FOUND, permErrorMsg ) );
                entity.setIsActive( requestPerm.getIsActive() );

                permEntities.add( entity );
            }
            rolePermsRepository.saveAll( permEntities );
        }

        /// menus
        if ( !requestMenus.isEmpty() ) {
            List< TbRoleMenus > menuEntities = new ArrayList<>();

            for ( RoleDto.ActiveRequest requestMenu : requestMenus ) {
                String permErrorMsg = "A non-existent roleMenuId exists. : " + requestMenu.getId();

                TbRoleMenus entity = roleMenusRepository.findById( requestMenu.getId() )
                        .orElseThrow( ( ) -> new ServiceException( ServiceMessageType.NOT_FOUND, permErrorMsg ) );
                entity.setIsActive( requestMenu.getIsActive() );

                menuEntities.add( entity );
            }
            roleMenusRepository.saveAll( menuEntities );
        }

        return true;
    }

}
