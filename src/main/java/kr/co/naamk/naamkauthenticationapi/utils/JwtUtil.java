package kr.co.naamk.naamkauthenticationapi.utils;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import kr.co.naamk.naamkauthenticationapi.exception.type.ServiceMessageType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Base64;
import java.util.Date;
import java.util.List;


@Slf4j
@Component
public class JwtUtil implements InitializingBean {

    /// access
    @Value("${jwt.access-token.sign-key}")
    private String accessSecretKey;
    private SecretKey ACCESS_SECRET_KEY;
    private final String AUTHORITIES_KEY = "naamk-auth-key";
    public static String ACCESS_HEADER = "Authorization";
    public static final long ACCESS_EXPIRATION = 8 * 60 * 60 * 1000; // hour * minute * second * milli =>  8시간


    /// 빈이 생성되고 주입을 받은 후에 secret값을 Base64 Decode해서 key 변수에 할당하기 위해
    @Override
    public void afterPropertiesSet( ) {
        // Base64 디코딩
        byte[] accessTokenSecretBytes = Base64.getDecoder().decode( accessSecretKey );
        this.ACCESS_SECRET_KEY = Keys.hmacShaKeyFor( accessTokenSecretBytes );
    }

    /// Access Token 생성
    public String createAccessToken( String username, List< String > roles ) {
        // 1️⃣ JWS 서명된 JWT 생성
        return Jwts.builder()
                .header().type( "JWT" ).and()
                .subject( username )
                .claim( AUTHORITIES_KEY, roles )
                .issuedAt( new Date() )
                .expiration( getExpirationDate( ACCESS_EXPIRATION ) )
                .signWith( ACCESS_SECRET_KEY, Jwts.SIG.HS256 ) // ✅ 최신 방식 적용
                .compact();
    }

    ///  검증
    public void validateToken( String token ) {
        getJWS( token );
    }


    /// Claim 파싱 (권한 정보)
    public Claims getClaimsFromToken( String token ) {
        try {
            return getJWS( token ).getPayload(); // ✅ JWT Payload (Claims) 반환
        } catch ( Throwable e ) {
            throw new JwtException( "Failed to get JWS payload", e );
        }
    }

    /// Claim 내 권한
    public List< SimpleGrantedAuthority > getAuthoritiesFromClaim( Claims claim ) {
        Object authorities = claim.get( AUTHORITIES_KEY );
        if ( authorities == null ) {
            throw new JwtException( ServiceMessageType.ERROR_NULL_DATA.getCode().toString() );
        }

        return ( (List< String >) claim.get( AUTHORITIES_KEY ) ).stream()
                .map( SimpleGrantedAuthority::new )
                .toList();
    }


    /// Claim 내 Username
    public String getUsernameFromClaim( Claims claim ) {
        try {
            return claim.getSubject();
        } catch ( Exception e ) {
            throw new JwtException( "Failed to get JWS subject", e.getCause() );
        }
    }


    /// JWS
    private Jws< Claims > getJWS( String token ) {
        try {
            return Jwts.parser()
                    .verifyWith( ACCESS_SECRET_KEY ) // ✅ 서명 검증 (최신 jjwt 사용 방식)
                    .build()
                    .parseSignedClaims( token ); // ✅ 서명된 JWT 파싱

        } catch ( ExpiredJwtException e ) {
            throw new JwtException( ServiceMessageType.EXPIRED_TOKEN.getCode().toString() );
        } catch ( Exception e ) {
            throw new JwtException( ServiceMessageType.INVALID_TOKEN.getCode().toString() );
        }
    }

    /// Expiration Date
    private Date getExpirationDate( long during ) {
        long nowMillis = System.currentTimeMillis();
        Date now = new Date( nowMillis );

        return new Date( now.getTime() + during );
    }
}
