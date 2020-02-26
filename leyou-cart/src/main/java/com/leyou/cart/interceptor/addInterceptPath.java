package com.leyou.cart.interceptor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class addInterceptPath   implements WebMvcConfigurer {

    @Autowired
    private CartUserInterceptor cartUserInterceptor;
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(cartUserInterceptor).addPathPatterns("/**");
    }
}
