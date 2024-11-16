package com.kwhackathon.broom.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
public class CorsMvcConfig {
    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        // 도메인 허용
        // corsConfiguration.addAllowedOrigin("*"); // 클라이언트 origin
        corsConfiguration.addAllowedOriginPattern("*"); // allowCredential과 사용시 쿠키 안 보내짐?
        // 모든 HTTP 메서드 허용
        corsConfiguration.addAllowedMethod("*");
        // 모든 헤더 허용
        corsConfiguration.addAllowedHeader("*");
        // CORS 요청에 대한 응답에서 쿠키를 허용
        corsConfiguration.setAllowCredentials(true);
        corsConfiguration.addExposedHeader("*");

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfiguration);

        return new CorsFilter(source);
    }
}
