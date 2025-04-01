package kr.co.naamk.naamkauthenticationapi.utils;

import kr.co.naamk.naamkauthenticationapi.exception.ServiceException;
import kr.co.naamk.naamkauthenticationapi.exception.type.ServiceMessageType;
import kr.co.naamk.naamkauthenticationapi.views.SecureProperties;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.spec.KeySpec;
import java.util.Base64;


@Component
@NoArgsConstructor
public class AESGCMEncryptionUtil {

    @Autowired
    private SecureProperties secureProperties;

    public String encrypt(String plaintext) {
        try {
            // 키와 IV 생성
            SecretKey key = generateKey();
            byte[] iv = generateIV();
            // 암호화
            return encrypt(plaintext, key, iv);
        } catch (Exception e) {
            throw new ServiceException( ServiceMessageType.ENCRYPT_ERR);
        }
    }

    public String decrypt(String encryptedText) {
        try {
            // 키와 IV 생성
            SecretKey key = generateKey();
            // 암호화
            return decrypt(encryptedText, key);
        } catch (Exception e) {
            throw new ServiceException(ServiceMessageType.DECRYPT_ERR);
        }
    }


    // 암호화 메서드
    private String encrypt(String plaintext, SecretKey key, byte[] iv) throws Exception {
        Cipher cipher = Cipher.getInstance(secureProperties.getPadding());
        GCMParameterSpec gcmParameterSpec = new GCMParameterSpec(secureProperties.getTagLang() * 8, iv);
        cipher.init(Cipher.ENCRYPT_MODE, key, gcmParameterSpec);

        byte[] ciphertext = cipher.doFinal(plaintext.getBytes(StandardCharsets.UTF_8));
        byte[] encryptedData = new byte[iv.length + ciphertext.length];
        System.arraycopy(iv, 0, encryptedData, 0, iv.length);
        System.arraycopy(ciphertext, 0, encryptedData, iv.length, ciphertext.length);

        return Base64.getEncoder()
                .encodeToString(encryptedData);
    }

    // 복호화 메서드
    private String decrypt(String ciphertext, SecretKey key) throws Exception {
        byte[] decodedData = Base64.getDecoder()
                .decode(ciphertext);
        byte[] iv = new byte[secureProperties.getIVLang()];
        System.arraycopy(decodedData, 0, iv, 0, iv.length);

        Cipher cipher = Cipher.getInstance(secureProperties.getPadding());
        GCMParameterSpec gcmParameterSpec = new GCMParameterSpec(secureProperties.getTagLang() * 8, iv);
        cipher.init(Cipher.DECRYPT_MODE, key, gcmParameterSpec);

        byte[] originalData = cipher.doFinal(decodedData, iv.length, decodedData.length - iv.length);
        return new String(originalData, StandardCharsets.UTF_8);
    }

    // 키 생성 메서드
    private SecretKey generateKey() throws Exception {
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        KeySpec spec = new PBEKeySpec(secureProperties.getSecretKey()
                .toCharArray(), secureProperties.getSalt()
                .getBytes(), 65536, 256);
        SecretKey tmp = factory.generateSecret(spec);
        SecretKeySpec secretKey = new SecretKeySpec(tmp.getEncoded(), secureProperties.getAlg());

        return secretKey;
    }

    // IV 생성 메서드
    private byte[] generateIV() {
        byte[] iv = new byte[secureProperties.getIVLang()];
        new java.security.SecureRandom().nextBytes(iv);
        return iv;
    }

}
