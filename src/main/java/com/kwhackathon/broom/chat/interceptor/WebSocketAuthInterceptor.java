package com.kwhackathon.broom.chat.interceptor;


import com.kwhackathon.broom.common.util.JwtUtil;
import lombok.AllArgsConstructor;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.List;

//STOMP 메시지의 Authorization 헤더에서 JWT 토큰을 추출
//JwtUtil을 사용하여 인증을 수행하고, SecurityContextHolder에 인증 정보를 설정
@Component
@AllArgsConstructor
// Spring Security보다 인터셉터의 우선 순위를 올리기 위해
//@Order(Ordered.HIGHEST_PRECEDENCE + 99)
public class WebSocketAuthInterceptor implements ChannelInterceptor {
    private final JwtUtil jwtUtil;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);

        // CONNECT 요청일 경우에만 인증 확인
        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
            List<String> authHeaders = accessor.getNativeHeader("Authorization");

            if (authHeaders == null || authHeaders.isEmpty()) {
                throw new RuntimeException("Authorization 헤더가 없습니다.");
            }

            String token = authHeaders.get(0);

            // Bearer 토큰인지 확인 후 "Bearer " 제거
            if (token.startsWith("bearer ")) {
                token = token.substring(7);
            } else {
                throw new RuntimeException("Authorization 형식이 잘못되었습니다.");
            }

            try {
                // StompPrincipal 생성 및 설정
                StompPrincipal principal = new StompPrincipal(jwtUtil.getUserId(token));
                accessor.setUser(principal);

                // 세션에 Principal 저장
                accessor.getSessionAttributes().put("principal", principal);

                // Spring Security의 SecurityContext에 Principal 설정
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(principal, null, null);

                    SecurityContextHolder.getContext().setAuthentication(authentication);

            } catch (Exception e) {
                throw new RuntimeException("JWT 검증 중 예외 발생");
            }
        }

        return message;
    }

}

