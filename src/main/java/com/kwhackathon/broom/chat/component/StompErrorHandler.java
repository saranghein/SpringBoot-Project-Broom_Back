package com.kwhackathon.broom.chat.component;
import org.springframework.messaging.Message;
import org.springframework.messaging.simp.stomp.*;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.StompSubProtocolErrorHandler;

@Component
public class StompErrorHandler extends StompSubProtocolErrorHandler {

    public StompErrorHandler() {
        super();
    }

    @Override
    public Message<byte[]> handleClientMessageProcessingError(Message<byte[]> clientMessage, Throwable ex) {
        // 클라이언트 메시지 처리 중 오류 발생 시 실행됨
        System.out.println("STOMP 메시지 처리 중 오류 발생: " + ex.getMessage());

        // 클라이언트에게 전송할 오류 메시지
        String errorMessage = "메시지 처리 중 오류가 발생했습니다.\n" + ex.getMessage();

        // STOMP ERROR 프레임을 생성하여 클라이언트에게 전송
        StompHeaderAccessor accessor = StompHeaderAccessor.create(StompCommand.ERROR);
        accessor.setMessage(errorMessage);
        accessor.setLeaveMutable(true);

        return MessageBuilder.createMessage(errorMessage.getBytes(), accessor.getMessageHeaders());
    }
}
