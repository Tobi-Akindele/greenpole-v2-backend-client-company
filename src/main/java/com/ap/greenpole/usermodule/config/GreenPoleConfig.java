package com.ap.greenpole.usermodule.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author Adewale Azeez <azeezadewale98@gmail.com>
 * @date 20-Aug-20 02:11 AM
 */
@Configuration
@EnableWebMvc
public class GreenPoleConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new GreenPoleMethodInterceptor());
    }

}
