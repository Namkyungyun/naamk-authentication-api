package kr.co.naamk.naamkauthenticationapi.web.service;

import kr.co.naamk.naamkauthenticationapi.domain.admin.TbAdminRoles;
import kr.co.naamk.naamkauthenticationapi.domain.admin.TbAdminUserRoles;
import kr.co.naamk.naamkauthenticationapi.domain.admin.TbAdminUsers;
import kr.co.naamk.naamkauthenticationapi.exception.ServiceException;
import kr.co.naamk.naamkauthenticationapi.exception.type.ServiceMessageType;
import kr.co.naamk.naamkauthenticationapi.mapstruct.UserMapper;
import kr.co.naamk.naamkauthenticationapi.utils.DateUtil;
import kr.co.naamk.naamkauthenticationapi.utils.SecurityUtil;
import kr.co.naamk.naamkauthenticationapi.web.dto.AdminAuthDto;
import kr.co.naamk.naamkauthenticationapi.web.dto.AdminUserDto;
import kr.co.naamk.naamkauthenticationapi.web.repository.AdminRoleRepository;
import kr.co.naamk.naamkauthenticationapi.web.repository.AdminUserRepository;
import kr.co.naamk.naamkauthenticationapi.web.repository.AdminUserRolesRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
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
        TbAdminUsers newUser = UserMapper.INSTANCE.ToEntity( dto );
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

        return UserMapper.INSTANCE.toDto( savedUser, expiredDate, authorityNames );
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

        return UserMapper.INSTANCE.toDto( user, expiredDate, authorityNames );


    }

    @Transactional
    public AdminUserDto.AccessResponse updateUserAccess( AdminUserDto.AccessRequest dto ) {
        AdminUserDto.AccessResponse result = AdminUserDto.AccessResponse.builder().build();

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

        result.setRoles( savedList );
        result.setId( user.getId() );
        result.setUsername( user.getUsername() );

        return result;
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
