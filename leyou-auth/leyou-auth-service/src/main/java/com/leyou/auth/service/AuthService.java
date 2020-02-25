package com.leyou.auth.service;

import com.leyou.auth.client.UserClient;
import com.leyou.auth.config.JwtConfigProperties;
import com.leyou.pojo.UserInfo;
import com.leyou.user.pojo.User;
import com.leyou.utils.JwtUtils;
import com.leyou.utils.RsaUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Service;

@Service
@EnableConfigurationProperties(JwtConfigProperties.class)
public class AuthService {
    @Autowired
    private UserClient userClient;
    @Autowired
    private JwtConfigProperties jwtConfigProperties;

    public String generateToken(String username, String password) {
        try {
        User user = userClient.queryByUser(username, password);
        if(user==null){
            return null;
        }
        UserInfo userInfo = new UserInfo(user.getId(), user.getUsername());

        return  JwtUtils.generateToken(userInfo, jwtConfigProperties.getPrivateKey(), jwtConfigProperties.getExpires());

        } catch (Exception e) {
e.printStackTrace();
//            throw  new RuntimeException();
        }
        return null;
    }
}
