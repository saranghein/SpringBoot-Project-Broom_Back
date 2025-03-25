package com.kwhackathon.broom.chat.component;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.messaging.Message;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.util.MimeType;
import org.springframework.web.socket.messaging.StompSubProtocolErrorHandler;

import java.time.LocalDateTime;

@Component
public class StompErrorHandler extends StompSubProtocolErrorHandler {
    private final ObjectMapper objectMapper = new ObjectMapper();

    public StompErrorHandler() {
        super();
    }

    @Override
    public Message<byte[]> handleClientMessageProcessingError(Message<byte[]> clientMessage, Throwable ex) {
        // 클라이언트 메시지 처리 중 오류 발생 시 실행됨
        System.out.println("STOMP 메시지 처리 중 오류 발생: " + ex.getMessage());

        // 닉네임 가져오기 (STOMP 헤더에서 추출)
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(clientMessage);
        String nickname = accessor.getFirstNativeHeader("nickname");
        if (nickname == null) {
            nickname = "알 수 없음";
        }
        // STOMP 상태 코드 설정 (1000번대)
        int statusCode;
        String statusMessage;

        if (ex instanceof IllegalArgumentException) {
            statusMessage = "유효하지 않은 데이터입니다.";
            statusCode = 1007; // Invalid Payload
        } else if (ex instanceof SecurityException) {
            statusMessage = "접근 권한이 없습니다.";
            statusCode = 1008; // Policy Violation
        } else {
            statusMessage = "알 수 없는 오류 발생";
            statusCode = 1011; // Internal Server Error
        }
        // 오류 메시지 생성 (ChatResponse.Message 사용)
//        ChatResponse.Message errorMessage = ChatResponse.Message.createErrorMessage(
//                statusMessage, statusCode, nickname
//        );

        // JSON 변환
        String jsonErrorMessage;
        try {
//            jsonErrorMessage = objectMapper.writeValueAsString(errorMessage);
        } catch (Exception e) {
            jsonErrorMessage = "{\"boardId\": \"\", \"message\": \"\", \"senderNickname\": \"" + nickname + "\", " +
                    "\"createdAt\": \"" + LocalDateTime.now() + "\", \"militaryBranch\": \"\", \"dischargeYear\": 0, \"expelled\": false, " +
                    "\"error\": {\"status\": \"JSON 변환 오류\", \"statusCode\": 1011}}";
        }

        // STOMP ERROR 프레임 생성
        StompHeaderAccessor errorAccessor = StompHeaderAccessor.create(StompCommand.ERROR);
        errorAccessor.setContentType(MimeType.valueOf("application/json"));
        errorAccessor.setLeaveMutable(true);

//        return MessageBuilder.createMessage(jsonErrorMessage.getBytes(StandardCharsets.UTF_8), errorAccessor.getMessageHeaders());
        return null;
    }
}
