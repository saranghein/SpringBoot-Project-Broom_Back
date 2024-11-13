package com.kwhackathon.broom.chatRoom.controller;

import com.kwhackathon.broom.carpool.entity.CarpoolBoard;
import com.kwhackathon.broom.carpool.service.CarpoolBoardService;
import com.kwhackathon.broom.chatRoom.dto.ChatRoomForCarpoolDto;
import com.kwhackathon.broom.chatRoom.dto.ChatRoomForEarlyDepartureDto;
import com.kwhackathon.broom.chatRoom.service.ChatRoomForCarpoolService;
import com.kwhackathon.broom.chatRoom.service.ChatRoomForEarlyDepartureBoardService;
import com.kwhackathon.broom.earlyDepartureBoard.entity.EarlyDepartureBoard;
import com.kwhackathon.broom.user.entity.User;
import com.kwhackathon.broom.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("earlyDeparture/chat/room")
@RequiredArgsConstructor
public class ChatRoomForEarlyDepartureController implements ChatRoomForEarlyDepartureOperation{
    private final ChatRoomForEarlyDepartureBoardService chatRoomForEarlyDepartureBoardService;

    private final EarlyDepartureService earlyDepartureService;

    private final UserService userService;

    @Override
    public ResponseEntity<?> getChatRoomList(User participant) {
        try {
            List<ChatRoomForEarlyDepartureDto.ResponseForGetChatRoomList> chatRoomList = chatRoomForEarlyDepartureBoardService.getChatRoomList(participant);
            return ResponseEntity.ok(chatRoomList);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("채팅방 목록 조회에 실패했습니다.");
        }    }

    @Override
    public ResponseEntity<?> getChatRoom(Long earlyDepartureBoardId, User participant) {
        try {
            // CarpoolBoard에서 게시물 작성자(author) 가져오기
            Optional<EarlyDepartureBoard> earlyDepartureBoard  = earlyDepartureService.getEarlyDepartureBoard(earlyDepartureBoardId);
            User author = earlyDepartureBoard.get().getUser(); // CarpoolBoard에 저장된 작성자

            // 채팅방 조회 및 DTO 변환
            ChatRoomForEarlyDepartureDto.ResponseForGetChatRoomList chatRoom = chatRoomForEarlyDepartureBoardService.getChatRoom(earlyDepartureBoard, author, participant);

            //ChatRoomForCarpoolDto.Response chatRoomDto = ChatRoomForCarpoolDto.Response.fromEntity(chatRoom, participant);
            return ResponseEntity.ok(chatRoom);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("채팅방 조회에 실패했습니다.");
        }    }

    @Override
    public ResponseEntity<?> createChatRoom(Long earlyDepartureBoardId, User participant) {
        try {
            // CarpoolBoard에서 게시물 작성자(author) 가져오기
            Optional<EarlyDepartureBoard> earlyDepartureBoard  = earlyDepartureService.getEarlyDepartureBoard(earlyDepartureBoardId);
            User author = earlyDepartureBoard.get().getUser(); // CarpoolBoard에 저장된 작성자
            if(participant.getUserId().equals(author.getUserId())){// participant와 author 가 같으면 채팅방 생성 안 됨
                return ResponseEntity.status(HttpStatus.CONFLICT).body("작성자와 참여자가 같아 채팅방이 생성되지 않습니다.");
            }

            ChatRoomForEarlyDepartureDto.ResponseForCreateChatRoomList chatRoomDto = chatRoomForEarlyDepartureBoardService.createOrGetChatRoom(earlyDepartureBoard, author, participant);
            return ResponseEntity.ok(chatRoomDto);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("채팅방 생성에 실패했습니다.");
        }    }

    @Override
    public ResponseEntity<?> deleteChatRoom(String earlyDepartureBoardId, User user) {
        try{
            chatRoomForEarlyDepartureBoardService.deleteChatRoom(earlyDepartureBoardId);
            return ResponseEntity.ok("채팅방 삭제에 성공했습니다.");
        }catch (Exception e){
            return ResponseEntity.badRequest().body("채팅방 삭제에 실패했습니다.");
        }    }
}
