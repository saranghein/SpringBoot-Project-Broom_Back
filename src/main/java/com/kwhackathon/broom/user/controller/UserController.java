package com.kwhackathon.broom.user.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.RestController;

import com.kwhackathon.broom.user.dto.request.SignupDto;
import com.kwhackathon.broom.user.dto.request.UpdatePasswordDto;
import com.kwhackathon.broom.user.dto.request.UpdateUserInfoDto;
import com.kwhackathon.broom.user.dto.request.ValidateIdDto;
import com.kwhackathon.broom.user.dto.request.ValidateNicknameDto;
import com.kwhackathon.broom.user.dto.response.TokenDto;
import com.kwhackathon.broom.user.service.UserService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class UserController implements UserOperations {
    private final UserService userService;

    @Override
    public ResponseEntity<?> signup(SignupDto signupDto) {
        try {
            userService.createUser(signupDto);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("회원가입에 실패했습니다");
        }
        return ResponseEntity.status(HttpStatus.CREATED).body("회원가입이 완료되었습니다");
    }

    @Override
    public ResponseEntity<?> validateId(ValidateIdDto validateIdDto) {
        if (userService.validateId(validateIdDto)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("이미 사용 중인 아이디입니다");
        }
        return ResponseEntity.status(HttpStatus.OK).body("사용 가능한 이이디입니다");
    }

    @Override
    public ResponseEntity<?> validateNickname(ValidateNicknameDto validateNicknameDto) {
        if (userService.validateNickname(validateNicknameDto)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("이미 사용 중인 닉네임입니다");
        }
        return ResponseEntity.status(HttpStatus.OK).body("사용 가능한 닉네임입니다");
    }

    @Override
    public ResponseEntity<?> reissue(HttpServletRequest request, HttpServletResponse response) {
        try {
            TokenDto tokens = userService.reissue(request.getCookies());

            response.setHeader("Authorization", "Bearer " + tokens.getAccessToken());
            response.addHeader(HttpHeaders.SET_COOKIE, createCookie(tokens.getRefreshToken(), 24 * 60 * 60).toString());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
        return ResponseEntity.status(HttpStatus.OK).body("토큰이 재발급되었습니다");
    }

    @Override
    public ResponseEntity<?> exit(HttpServletRequest request, HttpServletResponse response) {
        try {
            userService.deleteUser(request.getCookies());
            response.addHeader(HttpHeaders.SET_COOKIE, createCookie(null, 0).toString());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
        return ResponseEntity.status(HttpStatus.OK).body("회원탈퇴가 완료되었습니다");
    }

    private ResponseCookie createCookie(String value, int expiry) {
        return ResponseCookie.from("refresh", value)
                .httpOnly(true)
                // .secure(true)
                .path("/")
                .maxAge(expiry)
                .sameSite("None")
                .build();
    }

    @Override
    public ResponseEntity<?> getMypageInfo() {
        try {
            return ResponseEntity.status(HttpStatus.OK).body(userService.getMypageInfo());
        } catch (UsernameNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("사용자 정보를 찾을 수 없습니다");
        }
    }

    @Override
    public ResponseEntity<?> getMyInfo() {
        try {
            return ResponseEntity.status(HttpStatus.OK).body(userService.getUserInfo());
        } catch (UsernameNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("사용자 정보를 찾을 수 없습니다");
        }
    }

    @Override
    public ResponseEntity<?> updateUserInfo(UpdateUserInfoDto updateUserInfoDto) {
        try {
            userService.updateUserInfo(updateUserInfoDto);
        } catch (UsernameNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("사용자 정보를 찾을 수 없습니다");
        }
        return ResponseEntity.status(HttpStatus.OK).body("정보가 변경되었습니다");
    }

    @Override
    public ResponseEntity<?> updatePassword(UpdatePasswordDto updatePasswordDto) {
        try {
            userService.updatePassword(updatePasswordDto);
        } catch (UsernameNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("사용자 정보를 찾을 수 없습니다");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
        return ResponseEntity.status(HttpStatus.OK).body("비밀번호가 변경되었습니다");
    }
}
