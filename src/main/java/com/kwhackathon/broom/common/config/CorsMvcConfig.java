package com.kwhackathon.broom.common.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsMvcConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("https://broom.life")
                .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")
                .allowedHeaders("Access-Control-Allow-Origin")
                // .allowedHeaders("Access-Control-Allow-Credentials")
                // .allowedHeaders("Access-Control-Allow-Methods")
                // .allowedHeaders("Access-Control-Allow-Headers")
                .allowCredentials(true);
    }
}