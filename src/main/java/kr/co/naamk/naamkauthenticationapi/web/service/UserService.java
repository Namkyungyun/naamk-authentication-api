package kr.co.naamk.naamkauthenticationapi.web.service;

import kr.co.naamk.naamkauthenticationapi.domain.TbRoles;
import kr.co.naamk.naamkauthenticationapi.domain.TbUserRoles;
import kr.co.naamk.naamkauthenticationapi.domain.TbUsers;
import kr.co.naamk.naamkauthenticationapi.exception.ServiceException;
import kr.co.naamk.naamkauthenticationapi.exception.type.ServiceMessageType;
import kr.co.naamk.naamkauthenticationapi.mapstruct.UserMapper;
import kr.co.naamk.naamkauthenticationapi.utils.SecurityUtil;
import kr.co.naamk.naamkauthenticationapi.web.dto.AuthDto;
import kr.co.naamk.naamkauthenticationapi.web.dto.UserDto;
import kr.co.naamk.naamkauthenticationapi.web.repository.RoleRepository;
import kr.co.naamk.naamkauthenticationapi.web.repository.UserRepository;
import kr.co.naamk.naamkauthenticationapi.web.repository.UserRolesRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserRolesRepository userRolesRepository;

    private final SecurityUtil securityUtil;
    private final BCryptPasswordEncoder passwordEncoder;

    @Transactional
    public UserDto createUser( UserDto.CreateRequest dto ) {
        /// roles
        List< TbRoles > roles = roleRepository.findByIdInAndIsActiveTrue( dto.getRoleIds() );

        /// users : checking exist user
        Optional< TbUsers > user = userRepository.findByUsername( dto.getUsername() );
        if ( user.isPresent() ) {
            throw new ServiceException( ServiceMessageType.ALREADY_EXIST );
        }

        /// users : authorities
        List< GrantedAuthority > authorities = securityUtil.getAuthorities( roles );
        List< String > authorityNames = securityUtil.getAuthorityNames( authorities );

        /// new user
        TbUsers newUser = UserMapper.INSTANCE.ToEntity( dto );
        newUser.setPassword( passwordEncoder.encode( dto.getPassword() ) );
        newUser.setFailCnt( 0 );
        newUser.setChangedAt( Timestamp.valueOf( LocalDateTime.now() ) );

        TbUsers savedUser = userRepository.save( newUser );

        /// userRoles
        if ( !roles.isEmpty() ) {
            List< TbUserRoles > userRoles = new ArrayList<>();
            for ( TbRoles role : roles ) {
                TbUserRoles userRole = new TbUserRoles();
                userRole.setRole( role );
                userRole.setUser( savedUser );
                userRole.setIsActive( false );

                userRoles.add( userRole );
            }
            userRolesRepository.saveAll( userRoles );
        }


        /// password validDate
        LocalDateTime localDateTime = newUser.getChangedAt().toLocalDateTime();
        LocalDateTime newDateTime = localDateTime.plusDays(90);
        Timestamp expiredDate = Timestamp.valueOf(newDateTime);

        return UserMapper.INSTANCE.toDto( savedUser, expiredDate, authorityNames );
    }

    @Transactional
    public UserDto updateUser( UserDto.UpdateRequest dto ) {
        /// user
        TbUsers entity = userRepository.findById( dto.getId() )
                .orElseThrow( ( ) -> new ServiceException( ServiceMessageType.NOT_FOUND ) );

        entity.setName( dto.getName() );
        entity.setEmail( dto.getEmail() );
        entity.setIsActive( dto.getIsActive() );

        TbUsers user = userRepository.save( entity );

        /// role
        List< TbRoles > roles = userRolesRepository.findByUserId( dto.getId() ).stream()
                .filter( TbUserRoles::getIsActive )
                .map( TbUserRoles::getRole )
                .filter( TbRoles::getIsActive )
                .toList();

        List< GrantedAuthority > authorities = securityUtil.getAuthorities( roles );
        List< String > authorityNames = securityUtil.getAuthorityNames( authorities );

        /// password
        LocalDateTime localDateTime = entity.getChangedAt().toLocalDateTime();
        LocalDateTime newDateTime = localDateTime.plusDays(90);
        Timestamp expiredDate = Timestamp.valueOf(newDateTime);

        return UserMapper.INSTANCE.toDto( user, expiredDate,  authorityNames );



    }

    @Transactional
    public UserDto.AccessResponse updateUserAccess( UserDto.AccessRequest dto) {
        UserDto.AccessResponse result = UserDto.AccessResponse.builder().build();

        /// user roles
        if(dto.getRoles().isEmpty()) {
            throw new ServiceException( ServiceMessageType.EMPTY_REQUEST );
        }

        List< TbUserRoles > entities = userRolesRepository.findByUserId( dto.getUserId() );
        List< Integer > ids = entities.stream().map( TbUserRoles::getId ).toList();
        TbUsers user = entities.getFirst().getUser();

        for ( AuthDto.ActiveRequest request : dto.getRoles() ) {
            boolean contains = ids.contains( request.getId() );
            if ( contains ) {
                int index = ids.indexOf( request.getId() );
                TbUserRoles userRoles = entities.get( index );
                userRoles.setIsActive( request.getIsActive() );
            }
        }

        List< UserDto.UserRoleResponse > savedList = userRolesRepository.saveAll( entities ).stream()
                .map( el -> UserDto.UserRoleResponse.builder()
                        .id( el.getId() )
                        .roleName( el.getRole().getName() )
                        .isActive( el.getIsActive() )
                        .build()
                ).toList();

        result.setRoles( savedList );
        result.setUserId( user.getId() );
        result.setUsername( user.getUsername() );

        return result;
    }

    /// TODO password update
    public Map<String, Boolean> updatePassword() {

        return Map.of("result", true);
    }
}
