package kr.co.auth.naamk.naamkauthenticationapi.util;

import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.security.SecureRandom;
import java.util.Base64;

@Slf4j
public class JwtUtil {

    @Test
    @DisplayName("서명용 SecretKey 생성")
        // 📌 JWT 서명(HS256, HS384, HS512)을 위한 SecretKey 생성
    void generateJwtSigningKey( ) {
        //HS256 → 32바이트(256비트)

        // 1️⃣ 32바이트(256비트) 랜덤 키 생성 (HS256용)
        byte[] keyBytes = new byte[ 64 ]; // HS256 = 32바이트 / HS384 = 48바이트 / HS512 = 64바이트
        new SecureRandom().nextBytes( keyBytes );

        // 2️⃣ SecretKey 객체 생성

        SecretKey secretKey = Keys.hmacShaKeyFor( keyBytes );

        // 3️⃣ Base64 인코딩하여 저장 가능
        String base64Key = Base64.getEncoder().encodeToString( secretKey.getEncoded() );

        log.info( "SigningKey(Base64) : {}", base64Key );
        log.info( "SigningKey(Base64) length: {}", base64Key.length() );
    }

    @Test
    @DisplayName("암호화용 SecretKey 생성")
        // 📌 JWT 암호화(AES-GCM)을 위한 SecretKey 생성
    void generateEncryptionKey( ) {
        // 1️⃣ 32바이트(256비트) 랜덤 AES 키 생성 (AES-256 사용)
        byte[] keyBytes = new byte[ 64 ];
        new SecureRandom().nextBytes( keyBytes );

        // 2️⃣ AES SecretKey 생성
        SecretKey secretKey = new SecretKeySpec( keyBytes, "AES" );

        // 3️⃣ Base64 인코딩하여 저장 가능
        String base64Key = Base64.getEncoder().encodeToString( secretKey.getEncoded() );

        log.info( "EncryptionKey(Base64) : {}", base64Key );
        log.info( "EncryptionKey  length : {}", secretKey.getEncoded().length );
    }


}
