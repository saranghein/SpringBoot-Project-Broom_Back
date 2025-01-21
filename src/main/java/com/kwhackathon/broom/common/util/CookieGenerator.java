package com.kwhackathon.broom.common.util;

import org.springframework.stereotype.Component;

import jakarta.servlet.http.Cookie;

@Component
public class CookieGenerator {
    public Cookie generateCookie(String key, String value, int maxAge) {
        Cookie cookie = new Cookie(key, value);
        cookie.setMaxAge(maxAge);
        cookie.setPath("/");
        cookie.setSecure(true);
        cookie.setHttpOnly(true);

        return cookie;
    }
}
