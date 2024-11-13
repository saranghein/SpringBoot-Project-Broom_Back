package com.kwhackathon.broom.common.filter;

import java.io.PrintWriter;
import java.util.Collection;
import java.util.Iterator;

import io.jsonwebtoken.io.IOException;

import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kwhackathon.broom.common.dto.LoginResponseDto;
import com.kwhackathon.broom.common.util.JwtUtil;
import com.kwhackathon.broom.user.entity.User;
import com.kwhackathon.broom.user.util.MilitaryChaplain;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.Authentication;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.FilterChain;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class LoginFilter extends UsernamePasswordAuthenticationFilter{
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    // 스프링 시큐리티로 사용자 검증 진행
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException, IOException {
        setUsernameParameter("userId");
        String userId = obtainUsername(request);
        String password = obtainPassword(request);

        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                userId, password,
                null);

        return authenticationManager.authenticate(authToken);
    }

    // 로그인 성공시 실행
    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
            Authentication authentication) throws java.io.IOException {
        String userId = authentication.getName();
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        Iterator<? extends GrantedAuthority> iterator = authorities.iterator();
        String role = iterator.next().getAuthority();
        

        String accessToken = jwtUtil.createJwt("access", userId, role, 1000 * 60 * 1000L); // 1000분(테스트용)
        String refreshToken = jwtUtil.createJwt("refresh", userId, role, 24 * 60 * 60 * 1000L); // 24시간

        response.addHeader("Authorization", "Bearer " + accessToken); // 응답 헤더에 access토큰 설정
        response.addHeader(HttpHeaders.SET_COOKIE, createCookie("refresh", refreshToken).toString());// 응답시 쿠키에 refresh토큰 저장

        response.setStatus(HttpStatus.OK.value());

        // 로그인 성공 시 로그인 한 사용자의 닉네임, 군종을 반환
        User user = (User) authentication.getPrincipal();
        String nickname = user.getNickname();
        MilitaryChaplain militaryChaplain = user.getMilitaryChaplain();

        ObjectMapper objectMapper = new ObjectMapper();
        String userJsonResponse = objectMapper.writeValueAsString(new LoginResponseDto(nickname, militaryChaplain));
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json");
        try (PrintWriter writer = response.getWriter()) {
            writer.print(userJsonResponse);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 로그인 실패시 실행하는 메소드
    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response,
            AuthenticationException failed) throws java.io.IOException {
        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        response.setCharacterEncoding("UTF-8");
        response.setContentType("text/plain; charset=UTF-8");
        try (PrintWriter writer = response.getWriter()) {
            writer.print("아이디 또는 비밀번호가 일치하지 않습니다");
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 쿠키 생성
    private ResponseCookie createCookie(String key, String value) {
        return ResponseCookie.from(key, value)
                .httpOnly(true)
                // .secure(true)
                .path("/")
                .maxAge(24 * 60 * 60)
                .sameSite("None")
                .build();
    }
}
