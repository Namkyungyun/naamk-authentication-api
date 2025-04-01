package kr.co.naamk.naamkauthenticationapi.views;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Getter
@Setter
public class SecureProperties {

    @Value( "${secure.aes.gcm.padding}" )
    private String padding;

    @Value("${secure.aes.gcm.IVLang}")
    private Integer iVLang;

    @Value( "${secure.aes.gcm.secretKey}" )
    private String secretKey;

    @Value( "${secure.aes.gcm.salt}" )
    private String salt;

    @Value( "${secure.aes.gcm.tagLang}" )
    private Integer tagLang;

    @Value( "${secure.aes.gcm.alg}" )
    private String alg;
}
