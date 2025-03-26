package kr.co.naamk.naamkauthenticationapi.web.service;

import kr.co.naamk.naamkauthenticationapi.config.security.exception.SecurityException;
import kr.co.naamk.naamkauthenticationapi.domain.admin.*;
import kr.co.naamk.naamkauthenticationapi.exception.ServiceException;
import kr.co.naamk.naamkauthenticationapi.exception.type.ServiceMessageType;
import kr.co.naamk.naamkauthenticationapi.redis.model.RedisRoleEntity;
import kr.co.naamk.naamkauthenticationapi.redis.model.RedisTokenEntity;
import kr.co.naamk.naamkauthenticationapi.redis.repository.RedisRoleRepository;
import kr.co.naamk.naamkauthenticationapi.redis.repository.RedisTokenRepository;
import kr.co.naamk.naamkauthenticationapi.utils.DateUtil;
import kr.co.naamk.naamkauthenticationapi.utils.JwtUtil;
import kr.co.naamk.naamkauthenticationapi.utils.SecurityUtil;
import kr.co.naamk.naamkauthenticationapi.web.dto.AdminAuthDto;
import kr.co.naamk.naamkauthenticationapi.web.repository.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
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
import java.util.stream.StreamSupport;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminAuthService implements UserDetailsService {

    private final DateUtil dateUtil;
    private final JwtUtil jwtUtil;
    private final SecurityUtil securityUtil;
    private final BCryptPasswordEncoder passwordEncoder;

    private final AdminUserRepository adminUserRepository;
    private final AdminRoleRepository adminRoleRepository;

    private final AdminUserRolesRepository adminUserRolesRepository;
    private final AdminRoleMenusRepository adminRoleMenusRepository;
    private final AdminRolePermsRepository adminRolePermsRepository;

    private final RedisTokenRepository redisTokenRepository;
    private final RedisRoleRepository redisRoleRepository;

    public final long AUTH_EXPIRATION = 8 * 60 * 60 * 1000; // // hour * minute * second * milli =>  8시간
    private final SecurityException securityException;


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
    @Transactional
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

        /// db 저장 (유저 정보)
        user.setFailCnt( 0 );
        adminUserRepository.save( user );


        /// redis 저장 (유저 access token 값)
        redisTokenRepository.save( RedisTokenEntity.builder()
                        .username( username )
                        .accessToken( accessToken )
                        .timeToLive( JwtUtil.ACCESS_EXPIRATION )
                        .build() );


        /// security context 저장
        SecurityContextHolder.getContext().setAuthentication( authentication );


        return AdminAuthDto.LoginResponse.builder()
                .userId( user.getId() )
                .accessToken( accessToken )
                .expiredAt( expiredAt )
                .build();
    }


    public void logout( String username ) {
        RedisTokenEntity entity = redisTokenRepository.findByUsername( username );
        if ( entity != null ) {
            redisTokenRepository.delete( entity );
        }

        User principal = (User) securityUtil.getAuthentication().getPrincipal();
        if ( principal.getUsername().equals( username ) ) {
            securityUtil.clearContextHolder();
        }

    }


    @Transactional(readOnly = true)
    public Iterable< RedisRoleEntity > getRedisRoleAuthorities( ) {
        return redisRoleRepository.findAll();
    }


    /// Save role info in Redis (roleName, menus, perms)
    @Transactional
    public void refreshAuthorities( ) {
        try {

            List< TbAdminRoles > activeRoles = adminRoleRepository.findByIsActiveTrue();
            List< TbAdminRolePerms > activePerms = adminRolePermsRepository.findByIsActiveTrue();
            List< TbAdminRoleMenus > activeMenus = adminRoleMenusRepository.findByIsActiveTrue().stream()
                    .filter( el -> el.getMenu().getIsActive() )
                    .toList();


            Iterable< RedisRoleEntity > redisAll = redisRoleRepository.findAll();
            List< RedisRoleEntity > redisDeleteList = new ArrayList<>( StreamSupport.stream( redisAll.spliterator(), false ).toList() );

            List< RedisRoleEntity > redisSaveList = new ArrayList<>();
            for ( TbAdminRoles role : activeRoles ) {
                String roleName = role.getName();

                /// active false 목록을 레디스에서 삭제하기 위한 전처리
                Optional< RedisRoleEntity > updateElement = redisDeleteList.stream()
                        .filter( el -> el.getRoleName().equals( roleName ) )
                        .findFirst();
                updateElement.ifPresent( redisDeleteList::remove );


                /// menu
                List< String > menus = activeMenus.stream()
                        .filter( roleMenu -> Objects.equals( roleMenu.getRole().getId(), role.getId() ) )
                        .map( roleMenu -> roleMenu.getMenu().getCode() )
                        .toList();


                /// perms
                List< String > perms = activePerms.stream()
                        .filter( rolePerm -> Objects.equals( rolePerm.getRole().getId(), role.getId() ) )
                        .map( TbAdminRolePerms::getPermCd )
                        .toList();


                /// 저장
                redisSaveList.add( RedisRoleEntity.builder()
                        .roleName( roleName )
                        .perms( perms )
                        .menus( menus )
                        .timeToLive( AUTH_EXPIRATION )
                        .build() );

            }

            redisRoleRepository.deleteAll( redisDeleteList );
            redisRoleRepository.saveAll( redisSaveList );

        } catch ( Exception e ) {
            log.error( ServiceMessageType.ERROR_CACHE.getServiceMessage(), e );

            throw new ServiceException( ServiceMessageType.ERROR_CACHE );
        }
    }


    /**
     * DB의 역할에 조회하여 List<GrantedAuthority> 만들어 반환
     *
     * @param userId
     * @return
     */
    private List< GrantedAuthority > getAuthorities( Integer userId ) {
        List< TbAdminUserRoles > roles = adminUserRolesRepository.findByUserId( userId );
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
