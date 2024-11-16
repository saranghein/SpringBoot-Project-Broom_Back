package com.kwhackathon.broom.user.service;

import java.time.LocalDate;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.kwhackathon.broom.common.util.JwtUtil;
import com.kwhackathon.broom.user.dto.request.SignupDto;
import com.kwhackathon.broom.user.dto.request.UpdatePasswordDto;
import com.kwhackathon.broom.user.dto.request.UpdateUserInfoDto;
import com.kwhackathon.broom.user.dto.request.ValidateIdDto;
import com.kwhackathon.broom.user.dto.request.ValidateNicknameDto;
import com.kwhackathon.broom.user.dto.response.MypageInfoDto;
import com.kwhackathon.broom.user.dto.response.TokenDto;
import com.kwhackathon.broom.user.dto.response.UserInfoDto;
import com.kwhackathon.broom.user.entity.User;
import com.kwhackathon.broom.user.repository.UserRepository;
import com.kwhackathon.broom.user.util.Role;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.Cookie;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public void createUser(SignupDto dto) {
        userRepository.save(User.builder()
                .userId(dto.getUserId())
                .password(passwordEncoder.encode(dto.getPassword()))
                .nickname(dto.getNickname())
                .dischargeYear(dto.getDischargeYear())
                .militaryChaplain(dto.getMilitaryChaplain())
                .role(Role.MEMBER).build());
    }

    public boolean validateId(ValidateIdDto dto) {
        return userRepository.existsByUserId(dto.getUserId());
    }

    public boolean validateNickname(ValidateNicknameDto dto) {
        return userRepository.existsByNickname(dto.getNickname());
    }
    
    public TokenDto reissue(Cookie[] cookies) {
        String refreshToken = validateRefresh(cookies);

        String userId = jwtUtil.getUserId(refreshToken);
        String role = jwtUtil.getRole(refreshToken);

        // 새 토큰 발급
        String newAccess = jwtUtil.createJwt("access", userId, role, 600000L);
        String newRefresh = jwtUtil.createJwt("refresh", userId, role, 86400000L);

        return new TokenDto(newAccess, newRefresh);
    }

    public void deleteUser(Cookie[] cookies) throws NullPointerException, ExpiredJwtException,
            IllegalArgumentException {
        String refresh = validateRefresh(cookies);
                System.out.println(refresh);
        String userId = jwtUtil.getUserId(refresh);
        userRepository.deleteByUserId(userId);
    }

    public UserDetails loadUserByUsername(String userId) throws UsernameNotFoundException {
        return userRepository.findByUserId(userId)
                .orElseThrow(() -> new UsernameNotFoundException("사용자 정보를 찾을 수 없습니다."));
    }

    private String validateRefresh(Cookie[] cookies) throws NullPointerException, ExpiredJwtException,
            IllegalArgumentException {
        String refresh = null;
        if (cookies == null) {
            throw new NullPointerException("쿠키가 존재하지 않습니다");
        }

        for (Cookie cookie : cookies) {
            if (cookie.getName().equals("refresh")) {
                refresh = cookie.getValue();
            }
        }

        if (refresh == null) {
            throw new NullPointerException("토큰이 존재하지 않습니다");
        }

        try {
            jwtUtil.isExpired(refresh);
        } catch (ExpiredJwtException e) {
            throw new ExpiredJwtException(null, null, "refresh토큰이 만료되었습니다");
        }

        // 토큰이 refresh인지 확인 (발급시 페이로드에 명시)
        String category = jwtUtil.getCategory(refresh);
        if (!category.equals("refresh")) {
            throw new IllegalArgumentException("유효하지 않은 토큰입니다");
        }
        return refresh;
    }

    public MypageInfoDto getMypageInfo() {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = (User) loadUserByUsername(userId);
        int reserveYear = LocalDate.now().getYear() - user.getDischargeYear();
        return new MypageInfoDto(user.getNickname(), reserveYear);
    }

    public UserInfoDto getUserInfo() {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = (User) loadUserByUsername(userId);
        return new UserInfoDto(user.getNickname(), user.getDischargeYear(), user.getMilitaryChaplain());
    }

    public void updateUserInfo(UpdateUserInfoDto dto) {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = (User) loadUserByUsername(userId);
        user.updateUserInfo(dto);
    }

    public void updatePassword(UpdatePasswordDto dto) {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = (User) loadUserByUsername(userId);
        if (!passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("기존 비밀번호가 일치하지 않습니다");
        }
        user.updatePassword(passwordEncoder.encode(dto.getNewPassword()));
    }
}
