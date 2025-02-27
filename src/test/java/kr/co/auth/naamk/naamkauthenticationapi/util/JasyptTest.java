package kr.co.auth.naamk.naamkauthenticationapi.util;

import lombok.extern.slf4j.Slf4j;
import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.jasypt.iv.RandomIvGenerator;
import org.junit.jupiter.api.Test;

@Slf4j
public class JasyptTest {

    /* prod DB정보 암호화 */
    @Test
    public void stringEncryptor() {
        String url = "jdbc:postgresql://localhost:25432/web3?useunicode=true&characterencoding=utf-8";
        String username = "dev_poplus";
        String password = "Das%3bv-Bp15Z";
        String defaultSchema = "";

        log.info("[encrypt] url = {}", jasyptEncoding(url));
        log.info("[encrypt] defaultSchema = {}", jasyptEncoding(defaultSchema));
        log.info("[encrypt] username = {}", jasyptEncoding(username));
        log.info("[encrypt] password = {}", jasyptEncoding(password));
    }

    private String jasyptEncoding(String value) {
        String key = "naamk-password";
        StandardPBEStringEncryptor pbeStringEncryptor = new StandardPBEStringEncryptor();
        pbeStringEncryptor.setAlgorithm("PBEWITHHMACSHA512ANDAES_256");
        pbeStringEncryptor.setPassword(key);
        pbeStringEncryptor.setIvGenerator(new RandomIvGenerator());

        return pbeStringEncryptor.encrypt(value);
    }
}
