package com.leyou.cart.interceptor;

import com.leyou.cart.config.JwtProperties;
import com.leyou.pojo.UserInfo;
import com.leyou.utils.CookieUtils;
import com.leyou.utils.JwtUtils;
import com.leyou.utils.RsaUtils;
import org.apache.commons.lang3.StringUtils;
import org.bouncycastle.jcajce.provider.asymmetric.rsa.RSAUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
@Component
@EnableConfigurationProperties(JwtProperties.class)
public class CartUserInterceptor  extends HandlerInterceptorAdapter {

    @Autowired
    private JwtProperties jwtProperties;

    public static final ThreadLocal<UserInfo> tl = new ThreadLocal<>();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String token = CookieUtils.getCookieValue(request,  jwtProperties.getCookieName());
        if(StringUtils.isBlank(token)){
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            return false;
        }
        UserInfo userInfo = JwtUtils.getInfoFromToken(token, jwtProperties.getPublicKey());
        tl.set(userInfo);
        return true;

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        tl.remove();
    }

    public static UserInfo getLoginUser(){
        return tl.get();
    }
}
