package com.nix.jingxun.addp.web.common.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author keray
 * @date 2019/05/25 22:27
 */
@Configuration
public class CORSConfiguration implements WebMvcConfigurer {
    @Override
    //全局配置
    public void addCorsMappings(CorsRegistry registry) {
        registry
                .addMapping("/**")  //匹配访问的路径
                .allowedMethods("PUT", "GET" , "POST")           //匹配访问的方法
                .allowedOrigins("*")           //匹配允许跨域访问的源 "http://localhost:8081", "http://localhost:8082"
                .allowedHeaders("*")          //匹配允许头部访问
                .allowCredentials(true);
    }
}
