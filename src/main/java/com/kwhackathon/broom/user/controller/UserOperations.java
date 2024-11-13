package com.kwhackathon.broom.user.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.kwhackathon.broom.user.dto.request.SignupDto;
import com.kwhackathon.broom.user.dto.request.ValidateIdDto;
import com.kwhackathon.broom.user.dto.request.ValidateNicknameDto;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface UserOperations {
    @PostMapping("/signup")
    ResponseEntity<?> signup(@RequestBody SignupDto signupDto);

    @PostMapping("/validate-id")
    ResponseEntity<?> validateId(@RequestBody ValidateIdDto validateIdDto);

    @PostMapping("/validate-nickname")
    ResponseEntity<?> validateNickname(@RequestBody ValidateNicknameDto validateNicknameDto);
    
    @PostMapping("/reissue")
    ResponseEntity<?> reissue(HttpServletRequest request, HttpServletResponse response);

    @DeleteMapping("/exit")
    ResponseEntity<?> exit(HttpServletRequest request, HttpServletResponse response);
}