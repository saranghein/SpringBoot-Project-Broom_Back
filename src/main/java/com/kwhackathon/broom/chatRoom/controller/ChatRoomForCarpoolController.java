package com.kwhackathon.broom.chatRoom.controller;

import com.kwhackathon.broom.carpool.entity.CarpoolBoard;
import com.kwhackathon.broom.carpool.service.CarpoolBoardService;
import com.kwhackathon.broom.chatRoom.dto.ChatRoomForCarpoolDto;
import com.kwhackathon.broom.chatRoom.dto.ChatRoomListForCarpoolDto;
import com.kwhackathon.broom.chatRoom.service.ChatRoomForCarpoolService;
import com.kwhackathon.broom.user.entity.User;
import com.kwhackathon.broom.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("carpool/chat/room")
@RequiredArgsConstructor
public class ChatRoomForCarpoolController implements ChatRoomForCarpoolOperation {

    private final ChatRoomForCarpoolService chatRoomForCarpoolService;
    private final CarpoolBoardService carpoolBoardService;

    // 로그인 유저가 참여 중인 채팅방 목록 조회
    @Override
    public ResponseEntity<?> getChatRoomList(User participant) {
        try {
            List<ChatRoomForCarpoolDto.ResponseForGetChatRoomList> chatRoomList = chatRoomForCarpoolService.getChatRoomList(participant);
            ChatRoomListForCarpoolDto.Response response = new ChatRoomListForCarpoolDto.Response(chatRoomList);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("채팅방 목록 조회에 실패했습니다.");
        }

    }

    // 채팅방 생성 또는 기존 채팅방 ID 반환
    @Override
    public ResponseEntity<?> createChatRoom(
             Long carpoolBoardId,
             User participant) {
        try {
            // CarpoolBoard에서 게시물 작성자(author) 가져오기
            Optional<CarpoolBoard> carpoolBoard  = carpoolBoardService.getCarpoolBoard(carpoolBoardId);
            User author = carpoolBoard.get().getUser(); // CarpoolBoard에 저장된 작성자
            if(participant.getUserId().equals(author.getUserId())){// participant와 author 가 같으면 채팅방 생성 안 됨
                return ResponseEntity.status(HttpStatus.CONFLICT).body("작성자와 참여자가 같아 채팅방이 생성되지 않습니다.");
            }

            ChatRoomForCarpoolDto.ResponseForCreateChatRoomList chatRoomDto = chatRoomForCarpoolService.createOrGetChatRoom(carpoolBoard, author, participant);
            return ResponseEntity.ok(chatRoomDto);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("채팅방 생성에 실패했습니다.");
        }
    }

    @Override
    public ResponseEntity<?> deleteChatRoom(String chatRoomId, User user) {
        try{

            chatRoomForCarpoolService.deleteChatRoom(chatRoomId);
            return ResponseEntity.ok("채팅방 삭제에 성공했습니다.");
        }catch (Exception e){
            return ResponseEntity.badRequest().body("채팅방 삭제에 실패했습니다.");
        }
    }
}
