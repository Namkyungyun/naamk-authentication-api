package kr.co.naamk.naamkauthenticationapi.web.service.admin;

import kr.co.naamk.naamkauthenticationapi.domain.admin.TbAdminRoles;
import kr.co.naamk.naamkauthenticationapi.domain.admin.TbAdminUserRoles;
import kr.co.naamk.naamkauthenticationapi.domain.admin.TbAdminUsers;
import kr.co.naamk.naamkauthenticationapi.exception.ServiceException;
import kr.co.naamk.naamkauthenticationapi.exception.type.ServiceMessageType;
import kr.co.naamk.naamkauthenticationapi.mapstruct.admin.AdminUserMapper;
import kr.co.naamk.naamkauthenticationapi.utils.DateUtil;
import kr.co.naamk.naamkauthenticationapi.utils.SecurityUtil;
import kr.co.naamk.naamkauthenticationapi.web.dto.admin.AdminAuthDto;
import kr.co.naamk.naamkauthenticationapi.web.dto.admin.AdminUserDto;
import kr.co.naamk.naamkauthenticationapi.web.repository.admin.AdminRoleRepository;
import kr.co.naamk.naamkauthenticationapi.web.repository.admin.AdminUserRepository;
import kr.co.naamk.naamkauthenticationapi.web.repository.admin.AdminUserRolesRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Array;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AdminUserService {

    private final AdminUserRepository adminUserRepository;
    private final AdminRoleRepository adminRoleRepository;
    private final AdminUserRolesRepository adminUserRolesRepository;

    private final DateUtil dateUtil;
    private final SecurityUtil securityUtil;
    private final BCryptPasswordEncoder passwordEncoder;

    @Transactional
    public AdminUserDto createUser( AdminUserDto.CreateRequest dto ) {
        /// roles
        List< TbAdminRoles > roles = adminRoleRepository.findByIdInAndIsActiveTrue( dto.getRoleIds() );

        /// users : checking exist user
        Optional< TbAdminUsers > user = adminUserRepository.findByUsername( dto.getUsername() );
        if ( user.isPresent() ) {
            throw new ServiceException( ServiceMessageType.ALREADY_EXIST );
        }

        /// users : authorities
        List< GrantedAuthority > authorities = securityUtil.getAuthorities( roles );
        List< String > authorityNames = securityUtil.getAuthorityNames( authorities );

        /// new user
        TbAdminUsers newUser = AdminUserMapper.INSTANCE.ToEntity( dto );
        newUser.setPassword( passwordEncoder.encode( dto.getPassword() ) );
        newUser.setFailCnt( 0 );
        newUser.setChangedAt( dateUtil.getNow() );

        TbAdminUsers savedUser = adminUserRepository.save( newUser );

        /// userRoles
        if ( !roles.isEmpty() ) {
            List< TbAdminUserRoles > userRoles = new ArrayList<>();
            for ( TbAdminRoles role : roles ) {
                TbAdminUserRoles userRole = new TbAdminUserRoles();
                userRole.setRole( role );
                userRole.setUser( savedUser );
                userRole.setIsActive( false );

                userRoles.add( userRole );
            }
            adminUserRolesRepository.saveAll( userRoles );
        }


        /// password validDate
        LocalDateTime localDateTime = newUser.getChangedAt().toLocalDateTime();
        LocalDateTime newDateTime = localDateTime.plusDays( 90 );
        Timestamp expiredDate = Timestamp.valueOf( newDateTime );

        return AdminUserMapper.INSTANCE.toDto( savedUser, expiredDate, authorityNames );
    }

    @Transactional
    public AdminUserDto updateUser( AdminUserDto.UpdateRequest dto ) {
        /// user
        TbAdminUsers entity = adminUserRepository.findById( dto.getId() )
                .orElseThrow( ( ) -> new ServiceException( ServiceMessageType.NOT_FOUND ) );

        entity.setName( dto.getName() );
        entity.setEmail( dto.getEmail() );
        entity.setIsActive( dto.getIsActive() );

        TbAdminUsers user = adminUserRepository.save( entity );

        /// role
        List< TbAdminRoles > roles = adminUserRolesRepository.findByUserId( dto.getId() ).stream()
                .filter( TbAdminUserRoles::getIsActive )
                .map( TbAdminUserRoles::getRole )
                .filter( TbAdminRoles::getIsActive )
                .toList();

        List< GrantedAuthority > authorities = securityUtil.getAuthorities( roles );
        List< String > authorityNames = securityUtil.getAuthorityNames( authorities );

        /// 비멀번호 만료일
        Timestamp expiredDate = dateUtil.getExpiredAt(entity.getChangedAt());

        return AdminUserMapper.INSTANCE.toDto( user, expiredDate, authorityNames );
    }

    // 어드민에게 비활성화되어있는 모든 역할을 insert
    @Transactional(rollbackFor = Exception.class)
    public List<TbAdminUserRoles> delegateAllInactiveUserAccess(Integer id) {
        TbAdminUsers admin = adminUserRepository.findById( id )
                .orElseThrow( ( ) -> new ServiceException( ServiceMessageType.NOT_FOUND, "not found admin" ) );


        List< TbAdminRoles > allRoles = adminRoleRepository.findAll();

        // 사용자에게 부여된 RoleIds
        List< Integer > roleIds = adminUserRolesRepository.findByUserId( id ).stream()
                .map( TbAdminUserRoles::getRole )
                .map( TbAdminRoles::getId )
                .toList();

        List<TbAdminUserRoles> newAdminRoles = new ArrayList<>();
        for(TbAdminRoles role : allRoles) {
            boolean contains = roleIds.contains( role.getId() );
            if(contains) {
                continue;
            }

            TbAdminUserRoles adminRoles = new TbAdminUserRoles();
            adminRoles.setRole(role);
            adminRoles.setUser( admin );
            adminRoles.setIsActive( false );

            newAdminRoles.add( adminRoles );
        }

        if(!newAdminRoles.isEmpty()) {
            List< TbAdminUserRoles > adminUserRoles = adminUserRolesRepository.saveAll( newAdminRoles );
            newAdminRoles = adminUserRoles;
        }


        return newAdminRoles;
    }




    @Transactional(rollbackFor = Exception.class)
    public AdminUserDto.AccessResponse updateUserAccess( AdminUserDto.AccessRequest dto ) {
        /// user roles
        if ( dto.getRoles().isEmpty() ) {
            throw new ServiceException( ServiceMessageType.EMPTY_REQUEST );
        }

        List< TbAdminUserRoles > entities = adminUserRolesRepository.findByUserId( dto.getId() );

        List< Integer > ids = entities.stream().map( TbAdminUserRoles::getId ).toList();
        TbAdminUsers user = entities.getFirst().getUser();

        for ( AdminAuthDto.ActiveRequest request : dto.getRoles() ) {
            boolean contains = ids.contains( request.getId() );
            if ( contains ) {
                int index = ids.indexOf( request.getId() );
                TbAdminUserRoles userRoles = entities.get( index );
                userRoles.setIsActive( request.getIsActive() );
            }
        }

        List< AdminUserDto.UserRoleResponse > savedList = adminUserRolesRepository.saveAll( entities ).stream()
                .map( el -> AdminUserDto.UserRoleResponse.builder()
                        .id( el.getId() )
                        .roleName( el.getRole().getName() )
                        .isActive( el.getIsActive() )
                        .build()
                ).toList();


        return AdminUserDto.AccessResponse.builder()
                .roles( savedList )
                .id( user.getId() )
                .username( user.getUsername() )
                .build();
    }


    @Transactional
    public AdminUserDto.PasswordResponse updatePassword( AdminUserDto.PasswordRequest dto ) {

        /// checking user
        TbAdminUsers entity = adminUserRepository.findById( dto.getId() )
                .orElseThrow( ( ) -> new ServiceException( ServiceMessageType.NOT_FOUND ) );

        /// 기존 비밀번호과 비교
        String oldPassword = entity.getPassword();
        String newPassword = dto.getPassword();

        boolean matches = passwordEncoder.matches( newPassword, oldPassword );
        if ( matches ) {
            throw new ServiceException( ServiceMessageType.REQUEST_PARAM_ERROR, "The request password is same with old password." );
        }

        /// 암호화
        String encodedNewPassword = passwordEncoder.encode( newPassword );

        /// db저장
        entity.setPassword( encodedNewPassword );
        entity.setChangedAt( dateUtil.getNow() );
        TbAdminUsers savedEntity = adminUserRepository.save( entity );

        /// result에 값 세팅 후, 반환
        Timestamp expiredDate = dateUtil.getExpiredAt(entity.getChangedAt());

        return AdminUserDto.PasswordResponse.builder()
                .id( savedEntity.getId() )
                .expiredAt(expiredDate)
                .build();
    }


}
