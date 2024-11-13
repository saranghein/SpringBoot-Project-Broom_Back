package com.kwhackathon.broom.common.util;

import java.nio.charset.StandardCharsets;
import java.util.Date;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Jwts;

@Component
public class JwtUtil {
    private SecretKey secretKey;

    public JwtUtil(@Value("${spring.jwt.secret}") String secret) {
        this.secretKey = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8),
                Jwts.SIG.HS256.key().build().getAlgorithm());
    }

    // token에서 username을 추출(parsing)
    public String getUserId(String token) {
        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().get("userId",
                String.class);
    }

    // token에서 role을 추출(parsing)
    public String getRole(String token) {
        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().get("role",
                String.class);
    }

    // token에서 만료 일자를 추출(parsing)
    public Boolean isExpired(String token) {
        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().getExpiration()
                .before(new Date());
    }
    
    // access토큰인지 refresh토큰인지 카테고리 추출
    public String getCategory(String token) {
        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().get("category",
                String.class);
    }

    // 새 토큰을 생성
    public String createJwt(String category, String username, String role, Long expiredMs) {
        return Jwts.builder()
                .claim("category", category)    //access토큰인지 refresh토큰인지 표시
                .claim("userId", 
                        username)    // 페이로드에 userId(email)추가
                .claim("role", role)    // 페이로드에 role추가
                .issuedAt(new Date(System.currentTimeMillis())) // token이 발급되는 시점 기록
                .expiration(new Date(System.currentTimeMillis() + expiredMs))   //만료 기점 기록
                .signWith(secretKey)    // 암호화 진행
                .compact(); // 토큰 최종 발행
    }
}