package kr.co.naamk.naamkauthenticationapi.web.service.admin;

import kr.co.naamk.naamkauthenticationapi.domain.admin.*;
import kr.co.naamk.naamkauthenticationapi.exception.ServiceException;
import kr.co.naamk.naamkauthenticationapi.exception.type.ServiceMessageType;
import kr.co.naamk.naamkauthenticationapi.redis.model.RedisTokenEntity;
import kr.co.naamk.naamkauthenticationapi.redis.repository.RedisTokenRepository;
import kr.co.naamk.naamkauthenticationapi.utils.DateTimeUtil;
import kr.co.naamk.naamkauthenticationapi.utils.DateUtil;
import kr.co.naamk.naamkauthenticationapi.utils.JwtUtil;
import kr.co.naamk.naamkauthenticationapi.utils.SecurityUtil;
import kr.co.naamk.naamkauthenticationapi.web.dto.admin.AdminAuthDto;
import kr.co.naamk.naamkauthenticationapi.web.repository.admin.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminAuthService implements UserDetailsService {

    private final DateUtil dateUtil;
    private final JwtUtil jwtUtil;
    private final SecurityUtil securityUtil;
    private final BCryptPasswordEncoder passwordEncoder;

    private final AdminUserRepository adminUserRepository;
    private final AdminUserRolesRepository adminUserRolesRepository;

    private final RedisTokenRepository redisTokenRepository;


    @Override
    public UserDetails loadUserByUsername( String username ) throws UsernameNotFoundException {
        /// user
        TbAdminUsers user = adminUserRepository.findByUsername( username )
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
    @Transactional(rollbackFor = Exception.class)
    public AdminAuthDto.LoginResponse login( AdminAuthDto.LoginRequest dto ) {
        TbAdminUsers user = adminUserRepository.findByUsername( dto.getUsername() ) // username = login id
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
            adminUserRepository.save( user );

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

        /// expiredAt
        Timestamp expiredAt = dateUtil.getExpiredAt( user.getChangedAt() );

        // loginAt
        String loginAt = DateTimeUtil.getLocalDateTimeMilsNano();

        /// db 저장 (유저 정보)
        user.setFailCnt( 0 );
        adminUserRepository.save( user );


        /// redis 저장 (유저 access token 값)
        redisTokenRepository.save( RedisTokenEntity.builder()
                .username( username )
                .name( user.getName() )
                .accessToken( accessToken )
                .loginAt( loginAt )
                .timeToLive( JwtUtil.ACCESS_EXPIRATION )
                .build() );


        /// security context 저장
        SecurityContextHolder.getContext().setAuthentication( authentication );


        return AdminAuthDto.LoginResponse.builder()
                .accessToken( accessToken )
                .expiredAt( expiredAt )
                .build();
    }


    @Transactional(rollbackFor = Exception.class)
    public void logout( String username ) {

        // 현재 로그인한 유저를 로그아웃
        if ( username == null ) {
            username = securityUtil.getCurrentUserName();
            securityUtil.clearContextHolder();
        }

        RedisTokenEntity entity = redisTokenRepository.findByUsername( username );
        if ( entity != null ) {
            redisTokenRepository.delete( entity );
        }
    }


    /**
     * DB의 역할에 조회하여 List<GrantedAuthority> 만들어 반환
     *
     * @param userId
     * @return
     */
    private List< GrantedAuthority > getAuthorities( Integer userId ) {
        List< TbAdminUserRoles > roles = adminUserRolesRepository.findByUserId( userId ).stream()
                .filter( TbAdminUserRoles::getIsActive ).toList();
        List< GrantedAuthority > authorities = new ArrayList<>();

        if ( !roles.isEmpty() ) {
            List< TbAdminRoles > list = roles.stream().map( TbAdminUserRoles::getRole ).toList();
            authorities = securityUtil.getAuthorities( list );
        } else {
            authorities.add( new SimpleGrantedAuthority( "ANONYMOUS" ) );
        }
        return authorities;
    }
}
