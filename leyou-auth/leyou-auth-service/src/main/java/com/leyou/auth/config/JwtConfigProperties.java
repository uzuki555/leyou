package com.leyou.auth.config;

import com.leyou.utils.RsaUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;

import javax.annotation.PostConstruct;
import java.io.File;
import java.security.PrivateKey;
import java.security.PublicKey;

@ConfigurationProperties(prefix = "leyou.jwt")
public class JwtConfigProperties {
    private String privateKeyPath;
    private String publicKeyPath;
    private Integer expires;
    private String cookieName;
    private PublicKey publicKey;
    private PrivateKey privateKey;
    private String secret;
    public static final Logger logger = LoggerFactory.getLogger(JwtConfigProperties.class);
    @PostConstruct
    private void generateKey(){

        File privateKeyFile = new File(privateKeyPath);
        File publicKeyFile = new File(publicKeyPath);
        try {
        if(!privateKeyFile.exists() || !publicKeyFile.exists()){

                RsaUtils.generateKey(publicKeyPath,publicKeyPath,secret);

        }
        this.privateKey = RsaUtils.getPrivateKey(privateKeyPath);
        this.publicKey = RsaUtils.getPublicKey(publicKeyPath);
        } catch (Exception e) {
            logger.error("初始化公私钥匙失败！");
            throw new RuntimeException();

        }
    }

    public String getPrivateKeyPath() {
        return privateKeyPath;
    }

    public void setPrivateKeyPath(String privateKeyPath) {
        this.privateKeyPath = privateKeyPath;
    }

    public String getPublicKeyPath() {
        return publicKeyPath;
    }

    public void setPublicKeyPath(String publicKeyPath) {
        this.publicKeyPath = publicKeyPath;
    }

    public Integer getExpires() {
        return expires;
    }

    public void setExpires(Integer expires) {
        this.expires = expires;
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

    public PrivateKey getPrivateKey() {
        return privateKey;
    }

    public void setPrivateKey(PrivateKey privateKey) {
        this.privateKey = privateKey;
    }

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }
}
