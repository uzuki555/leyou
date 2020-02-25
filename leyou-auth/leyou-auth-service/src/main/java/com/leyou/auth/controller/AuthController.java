package com.leyou.auth.controller;

import com.leyou.auth.config.JwtConfigProperties;
import com.leyou.auth.service.AuthService;
import com.leyou.pojo.UserInfo;
import com.leyou.utils.CookieUtils;
import com.leyou.utils.JwtUtils;
import com.leyou.utils.RsaUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
@EnableConfigurationProperties(JwtConfigProperties.class)
public class AuthController {
    @Autowired
    private AuthService authService;
    @Autowired
    private JwtConfigProperties jwtConfigProperties;
    @PostMapping("/accredit")
    public ResponseEntity<Void> authUser(@RequestParam("username")String username,@RequestParam("password")String password,
                HttpServletRequest httpServletRequest,
                                         HttpServletResponse httpServletResponse
            ){


        String token = this.authService.generateToken(username,password);
        if(StringUtils.isBlank(token)){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        CookieUtils.setCookie(httpServletRequest,httpServletResponse,jwtConfigProperties.getCookieName(),token,jwtConfigProperties.getExpires()*60);
        return  ResponseEntity.ok(null);
    }

    @GetMapping("/verify")
    public ResponseEntity<UserInfo> verifyUser(@CookieValue("LY_TOKEN")String token,
                                               HttpServletResponse response,
                                               HttpServletRequest request){
        try {
            UserInfo userInfo = JwtUtils.getInfoFromToken(token, jwtConfigProperties.getPublicKey());

        if(userInfo==null){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        token = JwtUtils.generateToken(userInfo,jwtConfigProperties.getPrivateKey(),jwtConfigProperties.getExpires());
        CookieUtils.setCookie(request,response,jwtConfigProperties.getCookieName(),token,jwtConfigProperties.getExpires()*60);

        return ResponseEntity.ok(userInfo);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }
}
