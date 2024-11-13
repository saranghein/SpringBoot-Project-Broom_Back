package com.kwhackathon.broom.chatMessage.config;

import com.kwhackathon.broom.chatMessage.jwt.JwtChannelInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
@Configuration
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
    private final JwtChannelInterceptor jwtChannelInterceptor;

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // ws://localhost/chat
//        registry.addEndpoint("/chat")
//        .setAllowedOriginPatterns("*").withSockJS();
        System.out.println("WebSocket 엔드포인트 등록됨");
        registry.addEndpoint("/chat")
                .setAllowedOriginPatterns("*");//.withSockJS();
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {

        //메시지 브로커 설정
        registry.setPathMatcher(new AntPathMatcher("."));

        //sub
        //registry.enableSimpleBroker("/sub"); -> 내장 메시지 브로커
        // 외장 메시지 브로커(rabbit-mq)
        registry.enableStompBrokerRelay("/topic","/queue", "/exchange", "/amq/topic")
                .setRelayHost("localhost")
                .setRelayPort(61613)//rabbitMQ 기본 포트
                .setSystemLogin("guest")
                .setSystemPasscode("guest")
                .setClientLogin("guest")
                .setClientPasscode("guest")
                .setVirtualHost("/"); // virtual host 설정 추가


        //client로부터 메시지를 받을 api prefix 설정
        //pub
        registry.setApplicationDestinationPrefixes("/pub");
    }

    // 인증
    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(jwtChannelInterceptor);
    }
}
