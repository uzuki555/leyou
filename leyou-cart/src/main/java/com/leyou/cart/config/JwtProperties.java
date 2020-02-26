package com.leyou.cart.config;

import com.leyou.utils.RsaUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;

import javax.annotation.PostConstruct;
import java.io.File;
import java.security.PublicKey;

@ConfigurationProperties(prefix = "leyou.jwt")
public class JwtProperties {
    private String publicKeyPath;
    private String cookieName;
    private PublicKey publicKey;

    public static final Logger logger = LoggerFactory.getLogger(JwtProperties.class);
    @PostConstruct
    private void generateKey(){

        File publicKeyFile = new File(publicKeyPath);


        try {
            this.publicKey = RsaUtils.getPublicKey(publicKeyPath);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public String getPublicKeyPath() {
        return publicKeyPath;
    }

    public void setPublicKeyPath(String publicKeyPath) {
        this.publicKeyPath = publicKeyPath;
    }

    public String getCookieName() {
        return cookieName;
    }

    public void setCookieName(String cookieName) {
        this.cookieName = cookieName;
    }

    public PublicKey getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(PublicKey publicKey) {
        this.publicKey = publicKey;
    }
}
