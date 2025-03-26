package kr.co.naamk.naamkauthenticationapi.web.service;

import kr.co.naamk.naamkauthenticationapi.domain.admin.TbAdminMenus;
import kr.co.naamk.naamkauthenticationapi.domain.admin.TbAdminRoleMenus;
import kr.co.naamk.naamkauthenticationapi.domain.admin.TbAdminRolePerms;
import kr.co.naamk.naamkauthenticationapi.domain.admin.TbAdminRoles;
import kr.co.naamk.naamkauthenticationapi.domain.type.PermType;
import kr.co.naamk.naamkauthenticationapi.exception.ServiceException;
import kr.co.naamk.naamkauthenticationapi.exception.type.ServiceMessageType;
import kr.co.naamk.naamkauthenticationapi.mapstruct.RoleMapper;
import kr.co.naamk.naamkauthenticationapi.web.dto.AdminAuthDto;
import kr.co.naamk.naamkauthenticationapi.web.dto.AdminRoleDto;
import kr.co.naamk.naamkauthenticationapi.web.repository.AdminMenuRepository;
import kr.co.naamk.naamkauthenticationapi.web.repository.AdminRoleMenusRepository;
import kr.co.naamk.naamkauthenticationapi.web.repository.AdminRolePermsRepository;
import kr.co.naamk.naamkauthenticationapi.web.repository.AdminRoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
public class AdminRoleService {

    private final AdminRoleRepository adminRoleRepository;
    private final AdminMenuRepository adminMenuRepository;
    private final AdminRolePermsRepository adminRolePermsRepository;
    private final AdminRoleMenusRepository adminRoleMenusRepository;


    @Transactional
    public AdminRoleDto createRole( AdminRoleDto.CreateRequest dto ) {

        /// role
        Optional< TbAdminRoles > role = adminRoleRepository.findByName( dto.getName() );
        if ( role.isPresent() ) {
            throw new ServiceException( ServiceMessageType.ALREADY_EXIST, "The request name is already existing " );
        }

        TbAdminRoles entity = RoleMapper.INSTANCE.createDtoToEntity( dto );
        TbAdminRoles newRole = adminRoleRepository.save( entity );

        /// rolePerms
        List< TbAdminRolePerms > rolePerms = new ArrayList<>();
        List< PermType > perms = Arrays.stream( PermType.values() ).toList();
        for ( PermType perm : perms ) {
            TbAdminRolePerms rolePerm = new TbAdminRolePerms();
            rolePerm.setRole( newRole );
            rolePerm.setPermCd( perm.getCode() );
            rolePerm.setIsActive( false );

            rolePerms.add( rolePerm );
        }

        adminRolePermsRepository.saveAll( rolePerms );


        /// roleMenus
        List< TbAdminRoleMenus > roleMenus = new ArrayList<>();
        List< TbAdminMenus > menus = adminMenuRepository.findAll();
        for ( TbAdminMenus menu : menus ) {
            TbAdminRoleMenus roleMenu = new TbAdminRoleMenus();
            roleMenu.setRole( newRole );
            roleMenu.setMenu( menu );
            roleMenu.setIsActive( false );

            roleMenus.add( roleMenu );
        }
        adminRoleMenusRepository.saveAll( roleMenus );

        return RoleMapper.INSTANCE.toDto( newRole );
    }


    @Transactional
    public AdminRoleDto updateRole( AdminRoleDto.UpdateRequest dto ) {
        TbAdminRoles role = adminRoleRepository.findById( dto.getId() )
                .orElseThrow( ( ) -> new ServiceException( ServiceMessageType.NOT_FOUND ) );

        role.setDesc( dto.getDesc() );
        role.setIsActive( dto.getIsActive() );

        TbAdminRoles entity = adminRoleRepository.save( role );

        return RoleMapper.INSTANCE.toDto( entity );
    }

    @Transactional
    public AdminRoleDto.AccessResponse updateRoleAccess( AdminRoleDto.AccessRequest dto ) {
        AdminRoleDto.AccessResponse result = AdminRoleDto.AccessResponse.builder().build();

        /// role check
        TbAdminRoles role = adminRoleRepository.findById( dto.getRoleId() )
                .orElseThrow( ( ) -> new ServiceException( ServiceMessageType.NOT_FOUND ) );

        /// request arr check
        List< AdminAuthDto.ActiveRequest > requestPerms = dto.getPerms().stream().toList();
        List< AdminAuthDto.ActiveRequest > requestMenus = dto.getMenus();

        if ( requestPerms.isEmpty() && requestMenus.isEmpty() ) {
            throw new ServiceException( ServiceMessageType.EMPTY_REQUEST, "all requested parameters are empty." );
        }

        /// perms
        if ( !requestPerms.isEmpty() ) {
            List< TbAdminRolePerms > entities = adminRolePermsRepository.findByRole( role );
            List<Integer> ids = entities.stream().map( TbAdminRolePerms::getId ).toList();

            for ( AdminAuthDto.ActiveRequest request : requestPerms ) {
                boolean contains = ids.contains( request.getId() );
                if ( contains ) {
                    int index = ids.indexOf( request.getId() );
                    TbAdminRolePerms tbAdminRolePerms = entities.get( index );
                    tbAdminRolePerms.setIsActive( request.getIsActive() );
                }
            }

            List< AdminRoleDto.RolePermResponse > savedList = adminRolePermsRepository.saveAll( entities ).stream()
                    .map( el -> AdminRoleDto.RolePermResponse.builder()
                            .id( el.getId() )
                            .permCd( el.getPermCd() )
                            .isActive( el.getIsActive() )
                            .build() )
                    .toList();

            result.setPerms( savedList );
        }

        /// menus
        if ( !requestMenus.isEmpty() ) {
            List< TbAdminRoleMenus > entities = adminRoleMenusRepository.findByRole( role );
            List<Integer> ids = entities.stream().map( TbAdminRoleMenus::getId ).toList();

            for ( AdminAuthDto.ActiveRequest request : requestMenus ) {
                boolean contains = ids.contains( request.getId() );
                if ( contains ) {
                    int index = ids.indexOf( request.getId() );
                    TbAdminRoleMenus tbAdminRoleMenus = entities.get( index );
                    tbAdminRoleMenus.setIsActive( request.getIsActive() );
                }
            }

            List< AdminRoleDto.RoleMenuResponse > savedList = adminRoleMenusRepository.saveAll( entities ).stream()
                    .map( el -> AdminRoleDto.RoleMenuResponse.builder()
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
