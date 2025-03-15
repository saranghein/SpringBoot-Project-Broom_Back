package com.kwhackathon.broom.chat.controller;

import com.kwhackathon.broom.chat.component.ChatMessageProducer;
import com.kwhackathon.broom.chat.dto.ChatRequest;
import com.kwhackathon.broom.chat.dto.ChatResponse;
import com.kwhackathon.broom.chat.service.ChatService;
import com.kwhackathon.broom.participant.entity.Participant;
import com.kwhackathon.broom.participant.service.ParticipantService;
import com.kwhackathon.broom.user.entity.User;
import com.kwhackathon.broom.user.service.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@AllArgsConstructor
@Tag(name = "ChatController", description = "ChatController API 목록")
public class ChatControllerImpl implements ChatController {

    private final ChatService chatService;
    private final ParticipantService participantService;
    private final UserService userService;
    private final ChatMessageProducer chatMessageProducer;


    @Override
    @GetMapping("/chat/room/enter/{boardId}")
    public ResponseEntity<?> enterUser(@PathVariable String boardId, @AuthenticationPrincipal User user) {
        try{
            // 채팅방 참가자 확인
            Participant participant = participantService.findByUserIdAndBoardId(user.getUserId(), boardId);

            // 참가자 수 비교해서 넘어가면 못들어가게
            if (participantService.isFull(boardId) && participant==null) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body("채팅방에 인원이 가득 찼습니다.");
            }
            // 이미 참여한 사용자라면
            if (participant != null) {
                // 강퇴된 상태라면
                if(participant.getIsExpelled()){
                    return ResponseEntity.status(HttpStatus.LOCKED).body("강퇴된 사용자 입니다.");// Locked 423
                }
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("이미 참여한 채팅방입니다.");
            }

            // 참여자 등록
            String nickname=userService.loadUserByUsername(user.getUserId()).getNickname();
            participantService.addParticipant(user.getUserId(), boardId);
            return ResponseEntity.status(HttpStatus.CREATED).body(nickname+ "님이 입장하셨습니다.");

        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("잘못된 접근입니다.");
        }
    }

    // TODO : 메시지 받았을때도 강퇴됐는지 체크 필요
    // 채팅방에 있는 상태에서 채팅 보내기
    @MessageMapping("chat.message")
    public void sendMessage(@Payload ChatRequest.Message messageDto,SimpMessageHeaderAccessor headerAccessor) {
        try{
            Principal principal = (Principal) headerAccessor
                                        .getSessionAttributes()
                                        .get("principal");

            if (principal == null) {
                throw new IllegalStateException("인증정보가 없습니다.");
            }

            // 사용자 ID 추출
            String senderId = principal.getName();

            // 참가자 정보 조회
            Participant participant = participantService.findByUserIdAndBoardId(senderId, messageDto.getBoardId());

            // 참가자가 아니라면
            if (participant == null) {
                System.out.println("[Error] 채팅방 참가자 정보 없음: userId=" + senderId + ", boardId=" + messageDto.getBoardId());
                throw new IllegalStateException("채팅방에 참가하지 않았습니다.");
            }

            // 강퇴된 사용자라면
            if (participant.getIsExpelled()) {

                // TODO:고민필요
            }

            chatMessageProducer.sendMessage(messageDto, senderId);

        }catch (IllegalStateException e) {
            System.out.println("[Error] " + e.getMessage());
            throw e;
        } catch (Exception e) {
            System.out.println("[Unexpected Error] " + e.getMessage());
            throw new IllegalStateException("잘못된 접근입니다.");
        }
    }

    // 채팅방 입장시 이전 채팅 및 채팅방 정보 조회(?page=1&size=50)
    @GetMapping("/chat/room/{boardId}")
    public ResponseEntity<?> listChatMessages(
            @PathVariable String boardId,
            @AuthenticationPrincipal User user,
            @RequestParam(defaultValue = "0") int page, // 페이지
            @RequestParam(defaultValue = "50") int size // 개수
    ) {
        try {

            if (user == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("인증되지 않은 사용자입니다.");
            }
            // 채팅방 참가자 확인
            Participant participant = participantService.findByUserIdAndBoardId(user.getUserId(), boardId);

            // 참가자 수 비교해서 넘어가면 못들어가게
            if (participantService.isFull(boardId) && participant==null) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body("채팅방에 인원이 가득 찼습니다.");
            }
            // 새로 참여한 사용자라면
            if (participant == null) {
                String nickname=userService.loadUserByUsername(user.getUserId()).getNickname();
                participantService.addParticipant(user.getUserId(), boardId);
                return ResponseEntity.status(HttpStatus.CREATED).body(nickname+ "님이 입장하셨습니다.");
            }
            if(participant.getIsExpelled()){
                return ResponseEntity.status(HttpStatus.LOCKED).body("강퇴된 사용자 입니다.");// Locked 423
            }
            // 이전 메시지 및 채팅방 정보 조회
            ChatResponse.ChatRoomResponse response = chatService.getChatRoomInfo(boardId,page,size);
            return ResponseEntity.status(HttpStatus.OK).body(response);

        }
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("잘못된 접근입니다.");
        }
    }
}
