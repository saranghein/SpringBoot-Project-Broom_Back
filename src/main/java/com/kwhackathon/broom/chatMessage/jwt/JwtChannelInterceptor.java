package com.kwhackathon.broom.chatMessage.jwt;

import com.kwhackathon.broom.common.util.JwtUtil;
import com.kwhackathon.broom.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
@Component
public class JwtChannelInterceptor implements ChannelInterceptor {

    private final JwtUtil jwtUtil;

    @Autowired
    public JwtChannelInterceptor(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);

        // StompCommand.CONNECT 사용하여 CONNECT 메시지 검사
        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
            String token = accessor.getFirstNativeHeader("Authorization");

            if (token != null && token.startsWith("Bearer ")) {
                token = token.substring(7);
                if (!jwtUtil.isExpired(token)) {
                    String userId = jwtUtil.getUserId(token);
                    System.out.println(userId);
                    // StompPrincipal 생성 및 설정
                    StompPrincipal principal = new StompPrincipal(userId);
                    accessor.setUser(principal);
                    System.out.println("Principal set in accessor: " + accessor.getUser());

                    // 세션에 Principal 저장
                    accessor.getSessionAttributes().put("principal", principal);

                    // Spring Security의 SecurityContext에 Principal 설정
                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(principal, null, null);
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    System.out.println("Security context authentication set: " + SecurityContextHolder.getContext().getAuthentication());
                } else {
                    throw new IllegalArgumentException("Invalid JWT Token");
                }
            } else {
                throw new IllegalArgumentException("Missing Authorization Header");
            }
        }
        return message;
    }
}