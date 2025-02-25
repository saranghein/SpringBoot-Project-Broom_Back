package com.kwhackathon.broom.common.config;

import java.util.ArrayList;
import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.CorsConfiguration;

import com.kwhackathon.broom.common.filter.JwtFilter;
import com.kwhackathon.broom.common.filter.LoginFilter;
import com.kwhackathon.broom.common.filter.LogoutFilter;
import com.kwhackathon.broom.common.util.CookieGenerator;
import com.kwhackathon.broom.common.util.JwtGenerator;
import com.kwhackathon.broom.common.util.JwtUtil;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
        private final AuthenticationConfiguration authenticationConfiguration;
        private final JwtUtil jwtUtil;
        private final CookieGenerator cookieGenerator;
        private final JwtGenerator jwtGenerator;

        @Bean
        public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
                LoginFilter loginFilter = new LoginFilter(authenticationConfiguration.getAuthenticationManager(),
                                jwtGenerator,
                                cookieGenerator);
                loginFilter.setFilterProcessesUrl("/login");
                http.csrf((csrf) -> csrf.disable())
                                .formLogin((formLogin) -> formLogin.disable())
                                .logout((logout) -> logout.disable())
                                .httpBasic((httpBasic) -> httpBasic.disable())
                                .authorizeHttpRequests((authorizeRequest) -> authorizeRequest
                                                .requestMatchers("/", "/login", "/signup", "/validate-id",
                                                                "/validate-nickname",
                                                                "/reissue", "/exit", "/board/view/**",
                                                                "/board/search/**", "/bus/**", "/chat/**", "/date-tag")
                                                .permitAll()
                                                .requestMatchers("/admin/**").hasRole("ADMIN")
                                                .anyRequest().authenticated())
                                .addFilterAt(
                                                loginFilter,
                                                UsernamePasswordAuthenticationFilter.class)
                                .addFilterAfter(new JwtFilter(jwtUtil), LoginFilter.class)
                                .addFilterAfter(new LogoutFilter(jwtUtil,
                                                cookieGenerator), UsernamePasswordAuthenticationFilter.class)
                                .sessionManagement((session) -> session
                                                .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                                .cors((cors) -> cors.configurationSource(corsConfigSource()));
                return http.build();
        }

        @Bean
        public CorsConfigurationSource corsConfigSource() {
                return new CorsConfigurationSource() {
                        @Override
                        public CorsConfiguration getCorsConfiguration(HttpServletRequest request) {
                                CorsConfiguration configuration = new CorsConfiguration();

                                // 허용 경로 설정
                                // configuration.addAllowedOrigin("https://broom.life");
                                configuration.addAllowedHeader("*");
                                configuration.addAllowedOrigin("https://www.kym1n.com");
                                configuration.addAllowedOrigin(
                                                "https://broom-git-version20-kimdonggyuns-projects.vercel.app");

                                // Authorization 헤더를 응답에서 노출
                                configuration.setExposedHeaders(List.of("Authorization"));

                                // 허용 메서드 설정
                                configuration.setAllowedMethods(
                                                new ArrayList<>(List.of("GET", "POST", "PUT", "PATCH", "DELETE",
                                                                "OPTIONS")));

                                // credentials 허용
                                configuration.setAllowCredentials(true);

                                return configuration;
                        }
                };
        }

        @Bean
        public BCryptPasswordEncoder bCryptPasswordEncoder() {
                return new BCryptPasswordEncoder();
        }
}
