package kr.co.naamk.naamkauthenticationapi.config.security.filter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.lettuce.core.RedisException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
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

import javax.crypto.SecretKey;
import javax.security.sasl.AuthenticationException;
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

            // Access Token 검증
            if ( StringUtils.hasText( accessToken ) ) {

                SecretKey secretKey = jwtUtil.getSecretKey( true );
                jwtUtil.validateAccessToken( secretKey, accessToken );

                Claims claims = jwtUtil.getClaimsFromToken( secretKey, accessToken );
                username = jwtUtil.getUsernameFromClaim( claims );
                List< SimpleGrantedAuthority > authorities = jwtUtil.getAuthoritiesFromClaim( claims );

                // Redis에서 검증 (여기까지 왔다는 건 토큰 자체는 아직 만료가 되지 않았다는 것)
                validateTokenInRedis( username, accessToken ); // RedisException 연결

                // 인증 객체 등록
                UsernamePasswordAuthenticationToken authenticationToken = securityUtil.generateJWTAuthentication( request, username, authorities );
                SecurityContextHolder.getContext().setAuthentication( authenticationToken );

            }

            filterChain.doFilter( request, response );

        } catch ( Exception e ) {
            String message = e.getMessage(); // JWTUtil에서 온거라면 메시지로 ServiceMessageType의 코드값.
            log.error( "JwtAuthenticationFilter Error 발생: {}", message );
            if( e instanceof JwtException ) {
                if(message.contains( ServiceMessageType.EXPIRED_TOKEN.getCode().toString() )) {
                    // 만료됨.
                    log.error( "❌ Requested AccessToken is Expired" );
                    exception.unAuthorization( request, response, ServiceMessageType.EXPIRED_TOKEN );
                    return;
                }

                // 재로그인 필요.
                log.error( "❌ Need to check Redis token value" );
                exception.unAuthorization( request, response );


            } else if( e instanceof RedisException ) {
                // redis check
                if(message.contains( ServiceMessageType.EXPIRED_TOKEN.getCode().toString() )) {
                    // 만료됨.
                    log.error( "❌ Requested AccessToken is Expired" );
                    exception.unAuthorization( request, response, ServiceMessageType.EXPIRED_TOKEN );
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
