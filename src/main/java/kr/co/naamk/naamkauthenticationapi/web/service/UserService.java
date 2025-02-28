package kr.co.naamk.naamkauthenticationapi.web.service;

import kr.co.naamk.naamkauthenticationapi.domain.TbRoles;
import kr.co.naamk.naamkauthenticationapi.domain.TbUserRoles;
import kr.co.naamk.naamkauthenticationapi.domain.TbUsers;
import kr.co.naamk.naamkauthenticationapi.exception.ServiceException;
import kr.co.naamk.naamkauthenticationapi.exception.type.ServiceMessageType;
import kr.co.naamk.naamkauthenticationapi.mapstruct.UserMapper;
import kr.co.naamk.naamkauthenticationapi.utils.SecurityUtil;
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
    public UserDto.CreateResponse createUser( UserDto.CreateRequest dto ) {
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
        TbUsers newUser = UserMapper.INSTANCE.toEntity( dto );
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

                userRoles.add( userRole );
            }
            userRolesRepository.saveAll( userRoles );
        }


        return UserDto.CreateResponse.builder()
                .userId( savedUser.getId() )
                .username( savedUser.getUsername() )
                .authorities( authorityNames )
                .build();
    }
}
