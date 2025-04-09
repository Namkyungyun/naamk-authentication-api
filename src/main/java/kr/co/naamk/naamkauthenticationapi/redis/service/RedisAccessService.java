package kr.co.naamk.naamkauthenticationapi.redis.service;

import jakarta.servlet.http.HttpServletRequest;
import kr.co.naamk.naamkauthenticationapi.domain.admin.TbAdminRoleMenus;
import kr.co.naamk.naamkauthenticationapi.domain.admin.TbAdminRolePerms;
import kr.co.naamk.naamkauthenticationapi.domain.admin.TbAdminRoles;
import kr.co.naamk.naamkauthenticationapi.exception.ServiceException;
import kr.co.naamk.naamkauthenticationapi.exception.type.ServiceMessageType;
import kr.co.naamk.naamkauthenticationapi.redis.dto.RedisTokenDto;
import kr.co.naamk.naamkauthenticationapi.redis.model.RedisRoleEntity;
import kr.co.naamk.naamkauthenticationapi.redis.model.RedisTokenEntity;
import kr.co.naamk.naamkauthenticationapi.redis.repository.RedisRoleRepository;
import kr.co.naamk.naamkauthenticationapi.redis.repository.RedisTokenRepository;
import kr.co.naamk.naamkauthenticationapi.utils.JwtUtil;
import kr.co.naamk.naamkauthenticationapi.utils.SecurityUtil;
import kr.co.naamk.naamkauthenticationapi.views.SecureProperties;
import kr.co.naamk.naamkauthenticationapi.web.repository.admin.AdminRoleMenusRepository;
import kr.co.naamk.naamkauthenticationapi.web.repository.admin.AdminRolePermsRepository;
import kr.co.naamk.naamkauthenticationapi.web.repository.admin.AdminRoleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.StreamSupport;

@Slf4j
@Service
@RequiredArgsConstructor
public class RedisAccessService {

    private final AdminRoleRepository adminRoleRepository;
    private final AdminRoleMenusRepository adminRoleMenusRepository;
    private final AdminRolePermsRepository adminRolePermsRepository;

    private final JwtUtil jwtUtil;
    private final SecurityUtil securityUtil;
    private final RedisRoleRepository redisRoleRepository;
    private final RedisTokenRepository redisTokenRepository;
    public final long AUTH_EXPIRATION = 8 * 60 * 60 * 1000; // // hour * minute * second * milli =>  8시간
    private final SecureProperties secureProperties;

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

    @Transactional(readOnly = true)
    public RedisTokenDto findUserByAccessToken( HttpServletRequest request) {
        // 해당 함수 부르기 전에 이미지 필터에서 jwt로 검증은 가니까 바로 가져와도 됨.
        String accessToken = jwtUtil.getJwtAccessTokenFromRequest( request );
        if(accessToken == null) {
            throw new ServiceException( ServiceMessageType.SC_UNAUTHORIZED, "no access token" );
        }

        RedisTokenEntity redisTokenEntity = redisTokenRepository.findByAccessToken( accessToken )
                .orElseThrow( ( ) -> new ServiceException( ServiceMessageType.EXPIRED_TOKEN ) );

        List< String > currentUserRoles = securityUtil.getCurrentUserRoles();

        return RedisTokenDto.builder()
                .username( redisTokenEntity.getUsername() ) // 메뉴를 부르기 위한용도
                .name( redisTokenEntity.getName() ) // 프론트 기재용
                .loginAt( redisTokenEntity.getLoginAt() ) // 프론트기재용
                .roles( currentUserRoles ) // 사용자 역할
                .build();
    }
}
