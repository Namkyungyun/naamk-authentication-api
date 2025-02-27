package kr.co.naamk.naamkauthenticationapi.config.security.filter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.lettuce.core.RedisException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kr.co.naamk.naamkauthenticationapi.config.security.exception.SecurityException;
import kr.co.naamk.naamkauthenticationapi.exception.type.ServiceMessageType;
import kr.co.naamk.naamkauthenticationapi.redis.model.RedisTokenEntity;
import kr.co.naamk.naamkauthenticationapi.redis.repository.RedisTokenRepository;
import kr.co.naamk.naamkauthenticationapi.utils.JwtUtil;
import kr.co.naamk.naamkauthenticationapi.utils.SecurityUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;


@Component
@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final SecurityUtil securityUtil;
    private final RedisTokenRepository redisTokenRepository;
    private final SecurityException exception;

    @Override
    protected void doFilterInternal( HttpServletRequest request,
                                     HttpServletResponse response,
                                     FilterChain filterChain ) throws IOException {
        String username = "";
        String accessToken = "";

        try {
            accessToken = getJwtAccessTokenFromRequest( request );

            if ( StringUtils.hasText( accessToken ) ) {
                // Access Token 검증
                jwtUtil.validateToken( accessToken ); // 만료, 조작된 토큰 -> jwt exception

                Claims claims = jwtUtil.getClaimsFromToken( accessToken );
                username = jwtUtil.getUsernameFromClaim( claims );
                List< SimpleGrantedAuthority > authorities = jwtUtil.getAuthoritiesFromClaim( claims );

                // Redis에서 검증
                validateTokenInRedis( username, accessToken ); // 없음, 다른값 ->  redis exception

                // 인증 객체 등록
                UsernamePasswordAuthenticationToken authenticationToken = securityUtil.generateJWTAuthentication( request, username, authorities );
                SecurityContextHolder.getContext().setAuthentication( authenticationToken );

            }

            filterChain.doFilter( request, response );

        } catch ( Exception e ) {
            // JWTUtil에서 온거라면 메시지로 ServiceMessageType의 코드값.
            String message = e.getMessage();
            log.error( "JwtAuthenticationFilter Error 발생: {}", message );

            ServiceMessageType expirationType = ServiceMessageType.EXPIRED_TOKEN;
            ServiceMessageType invalidType = ServiceMessageType.INVALID_TOKEN;

            if( e instanceof JwtException ) {
                if(message.contains( expirationType.getCode().toString() )) {
                    // 만료
                    log.error( "❌ Requested AccessToken is Expired" );
                    exception.unAuthorization( request, response, expirationType );
                    return;
                }

                // 잘못된 token 값.
                log.error( "❌ Invalid token value" );
                exception.unAuthorization( request, response, invalidType);


            } else if( e instanceof RedisException ) {
                // redis check
                if(message.contains( expirationType.getCode().toString() )) {
                    // 만료됨.
                    log.error( "❌ Requested AccessToken is Expired" );
                    exception.unAuthorization( request, response, expirationType);
                    return;
                }

                // 재로그인 필요.
                exception.unAuthorization( request, response);
            } else {
                exception.unAuthorization( request, response );
            }
        }
    }


    private String getJwtAccessTokenFromRequest( HttpServletRequest request ) {
        String bearerToken = request.getHeader( JwtUtil.ACCESS_HEADER );
        if ( StringUtils.hasText( bearerToken ) && bearerToken.startsWith( "Bearer " ) ) {
            return bearerToken.substring( 7 );
        }

        return null;
    }

    private void validateTokenInRedis(String username, String accessToken)  {
        if(username.isBlank() || accessToken.isBlank()) {
            throw new RedisException(ServiceMessageType.INVALID_TOKEN.getCode().toString());
        }

        RedisTokenEntity accessTokenEntity = redisTokenRepository.findByUserId( username );
        if( accessTokenEntity == null ) {
            log.error( "❌ Not Found Access Token in Redis" );
            throw new RedisException(ServiceMessageType.EXPIRED_TOKEN.getCode().toString());
        }

        if ( !accessTokenEntity.getAccessToken().equals( accessToken ) ) {
            log.error( "❌ Exist Other AccessToken in Redis" );
            throw new RedisException(ServiceMessageType.INVALID_TOKEN.getCode().toString());
        }
    }
}
