package kr.co.naamk.naamkauthenticationapi.web.service;

import kr.co.naamk.naamkauthenticationapi.domain.TbRoles;
import kr.co.naamk.naamkauthenticationapi.domain.TbUserRole;
import kr.co.naamk.naamkauthenticationapi.domain.TbUsers;
import kr.co.naamk.naamkauthenticationapi.exception.ServiceException;
import kr.co.naamk.naamkauthenticationapi.exception.type.ServiceMessageType;
import kr.co.naamk.naamkauthenticationapi.redis.model.RedisTokenEntity;
import kr.co.naamk.naamkauthenticationapi.redis.repository.RedisTokenRepository;
import kr.co.naamk.naamkauthenticationapi.utils.JwtUtil;
import kr.co.naamk.naamkauthenticationapi.utils.SecurityUtil;
import kr.co.naamk.naamkauthenticationapi.web.dto.AuthDto;
import kr.co.naamk.naamkauthenticationapi.web.repository.UserRepository;
import kr.co.naamk.naamkauthenticationapi.web.repository.UserRoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthService implements UserDetailsService {

    private final JwtUtil jwtUtil;
    private final SecurityUtil securityUtil;
    private final BCryptPasswordEncoder passwordEncoder;

    private final UserRepository userRepository;
    private final UserRoleRepository userRoleRepository;
    private final RedisTokenRepository redisTokenRepository;


    @Override
    public UserDetails loadUserByUsername( String username ) throws UsernameNotFoundException {
        /// user
        TbUsers user = userRepository.findByUsername( username )
                .orElseThrow( ( ) -> new ServiceException( ServiceMessageType.NOT_FOUND, "user not found" ) );

        /// authorities
        List< GrantedAuthority > authorities = getAuthorities( user.getId() );

        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                authorities
        );
    }


    /**
     * 로그인 (로그인 시, refreshToken과 accessToken 무조건 새로운 값으로 저장.)
     *
     * @param dto
     * @return
     */
    @Transactional
    public AuthDto.LoginResponse login( AuthDto.LoginRequest dto ) {
        TbUsers user = userRepository.findByUsername( dto.getUsername() ) // username = login id
                .orElseThrow( ( ) -> new ServiceException( ServiceMessageType.NOT_FOUND, "user not found" ) );

        /// check active
        if ( !user.getIsActive() ) {
            throw new ServiceException( ServiceMessageType.FAIL_LOGIN, "비활성화된 계정입니다. 관리자에게 문의주세요." );
        }


        /// check password fail count
        if ( user.getFailCnt() == 5 ) {
            throw new ServiceException( ServiceMessageType.FAIL_LOGIN, "최대 로그인 시도 횟수를 초과하였습니다. 관리자에게 문의주세요." );
        }


        /// check password
        boolean matches = passwordEncoder.matches( dto.getPassword(), user.getPassword() );
        if ( !matches ) {
            Integer failCnt = user.getFailCnt() + 1;
            user.setFailCnt( failCnt );
            userRepository.save( user );

            throw new ServiceException( ServiceMessageType.FAIL_LOGIN, "잘못된 비밀번호입니다." );
        }


        /// generate Authentication (Security context에 저장)
        String username = dto.getUsername();
        String rawPassword = dto.getPassword();
        List< GrantedAuthority > authorities = getAuthorities( user.getId() );

        Authentication authentication = securityUtil.generateLoginAuthentication( username, rawPassword, authorities );


        /// generate accessToken refreshToken
        List< String > authorityTexts = authorities.stream().map( GrantedAuthority::getAuthority ).toList();
        String accessToken = jwtUtil.createAccessToken( username, authorityTexts );
        String refreshToken = jwtUtil.createRefreshToken( username );


        /// db 저장 (유저 정보)
        user.setFailCnt( 0 );
        user.setRefreshToken( refreshToken );
        user.setChangedAt( Timestamp.valueOf( LocalDateTime.now() ) );
        userRepository.save( user );


        /// redis 저장 (유저 access token 값)
        saveAccessToken(username, accessToken);


        /// security context 저장
        SecurityContextHolder.getContext().setAuthentication( authentication );


        return AuthDto.LoginResponse.builder()
                .userId( user.getId() )
                .refreshToken( refreshToken )
                .accessToken( accessToken )
                .build();
    }


    /**
     * refreshToken을 이용해 accessToken을 갱신
     * 클라이언트에서는 FAIL_REFRESH 코드 값인 '4006'이면 로그인 페이지로 되돌리기.
     *
     * @param dto : userId, refreshToken
     * @return : userId, username, accessToken
     */
    public AuthDto.RefreshResponse refresh( AuthDto.RefreshRequest dto ) {

        TbUsers user = userRepository.findById( dto.getUserId() )
                .orElseThrow( ( ) -> new ServiceException( ServiceMessageType.NOT_FOUND, "user not found" ) );

        if ( !user.getIsActive() ) {
            throw new ServiceException( ServiceMessageType.FAIL_REFRESH, "비활성화 상태의 유저입니다." );
        }

        if ( !user.getRefreshToken().equals( dto.getRefreshToken() ) ) {
            throw new ServiceException( ServiceMessageType.FAIL_REFRESH, "잘못된 리프레시값 입니다." );
        }

        List< GrantedAuthority > authorities = getAuthorities( user.getId() );
        List< String > authorityTexts = authorities.stream().map( GrantedAuthority::getAuthority ).toList();
        String accessToken = jwtUtil.createAccessToken( user.getUsername(), authorityTexts );

        saveAccessToken(user.getUsername(), accessToken);


        return AuthDto.RefreshResponse.builder()
                .userId( user.getId() )
                .username( user.getUsername() )
                .accessToken( accessToken )
                .build();
    }


    /**
     * DB의 역할에 조회하여 List<GrantedAuthority> 만들어 반환
     *
     * @param userId
     * @return
     */
    private List< GrantedAuthority > getAuthorities( Integer userId ) {
        List< TbUserRole > roles = userRoleRepository.findByUserId( userId );
        List< GrantedAuthority > authorities = new ArrayList<>();

        if ( !roles.isEmpty() ) {
            List< TbRoles > list = roles.stream().map( TbUserRole::getRole ).toList();
            authorities = securityUtil.getAuthorities( list );
        } else {
            authorities.add( new SimpleGrantedAuthority( "ANONYMOUS" ) );
        }

        return authorities;
    }

    private void saveAccessToken(String username, String accessToken) {
        redisTokenRepository.save(
                RedisTokenEntity.builder()
                        .userId( username )
                        .accessToken( accessToken )
                        .timeToLive( JwtUtil.ACCESS_EXPIRATION )
                        .build() );
    }

}
