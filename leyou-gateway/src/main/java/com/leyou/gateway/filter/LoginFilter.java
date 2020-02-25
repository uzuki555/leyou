package com.leyou.gateway.filter;

import com.leyou.gateway.config.FilterProperties;
import com.leyou.gateway.config.JwtConfigProperties;
import com.leyou.utils.CookieUtils;
import com.leyou.utils.JwtUtils;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Component
@EnableConfigurationProperties({ConfigurationProperties.class, FilterProperties.class})
public class LoginFilter extends ZuulFilter {
    @Autowired
    private JwtConfigProperties jwtConfigProperties;
    @Autowired
    private FilterProperties filterProperties;
    @Override
    public String filterType() {
        return "pre";
    }

    @Override
    public int filterOrder() {
        return 10;
    }

    @Override
    public boolean shouldFilter() {
        RequestContext context  = RequestContext.getCurrentContext();
        HttpServletRequest request = context.getRequest();
        String uri = request.getRequestURI().toString();
        List<String> allowPaths = filterProperties.getAllowPaths();
        for (String allowPath : allowPaths){
            if(StringUtils.contains(uri,allowPath)){
                return  false;
            }
        }
        return true;
    }

    @Override
    public Object run() throws ZuulException {
        RequestContext context  = RequestContext.getCurrentContext();
        HttpServletRequest request = context.getRequest();
        String token = CookieUtils.getCookieValue(request, jwtConfigProperties.getCookieName());
        try {
            JwtUtils.getInfoFromToken(token, jwtConfigProperties.getPublicKey());
        } catch (Exception e) {
            context.setSendZuulResponse(false);
            context.setResponseStatusCode(HttpStatus.UNAUTHORIZED.value());
        }
        return null;
    }
}
