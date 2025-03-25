package com.kwhackathon.broom.chat.interceptor;

import org.springframework.http.server.ServerHttpRequest;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;

import java.security.Principal;
import java.util.Map;
import java.util.UUID;

// 세션이 생성할 때 고유 ID 생성
public class HandshakeHandler extends DefaultHandshakeHandler {
    @Override
    protected Principal determineUser(ServerHttpRequest request, WebSocketHandler wsHandler, Map<String, Object> attributes) {
        try {
            return new StompPrincipal(UUID.randomUUID().toString());

        } catch (Exception e) {
            return null;
        }
    }
}
