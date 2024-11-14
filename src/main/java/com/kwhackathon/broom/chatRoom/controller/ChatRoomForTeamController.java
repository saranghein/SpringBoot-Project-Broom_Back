package com.kwhackathon.broom.chatRoom.controller;

import com.kwhackathon.broom.chatRoom.dto.ChatRoomForTeamDto;
import com.kwhackathon.broom.chatRoom.service.ChatRoomForTeamService;
import com.kwhackathon.broom.team.entity.TeamBoard;
import com.kwhackathon.broom.team.service.TeamBoardService;
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
@RequestMapping("team/chat/room")
@RequiredArgsConstructor
public class ChatRoomForTeamController implements ChatRoomForTeamOperation {
    private final ChatRoomForTeamService chatRoomForTeamService;
    private final UserService userService;
    private final TeamBoardService teamBoardService;

    @Override
    public ResponseEntity<?> getChatRoomList(User participant) {
        try {
            List<ChatRoomForTeamDto.ResponseForGetChatRoomList> chatRoomList = chatRoomForTeamService.getChatRoomList(participant);
            return ResponseEntity.ok(chatRoomList);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("채팅방 목록 조회에 실패했습니다.");
        }    }

    @Override
    public ResponseEntity<?> getChatRoom(Long teamBoardId, User participant) {
        try {
            // CarpoolBoard에서 게시물 작성자(author) 가져오기
            Optional<TeamBoard> teamBoard  = teamBoardService.getTeamBoard(teamBoardId);
            User author = teamBoard.get().getUser(); // CarpoolBoard에 저장된 작성자

            // 채팅방 조회 및 DTO 변환
            ChatRoomForTeamDto.ResponseForGetChatRoomList chatRoom = chatRoomForTeamService.getChatRoom(teamBoard, author, participant);

            //ChatRoomForCarpoolDto.Response chatRoomDto = ChatRoomForCarpoolDto.Response.fromEntity(chatRoom, participant);
            return ResponseEntity.ok(chatRoom);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("채팅방 조회에 실패했습니다.");
        }    }

    @Override
    public ResponseEntity<?> createChatRoom(Long teamBoardId, User participant) {
        try {
            // CarpoolBoard에서 게시물 작성자(author) 가져오기
            Optional<TeamBoard> teamBoard  = teamBoardService.getTeamBoard(teamBoardId);
            User author = teamBoard.get().getUser(); // CarpoolBoard에 저장된 작성자
            if(participant.getUserId().equals(author.getUserId())){// participant와 author 가 같으면 채팅방 생성 안 됨
                return ResponseEntity.status(HttpStatus.CONFLICT).body("작성자와 참여자가 같아 채팅방이 생성되지 않습니다.");
            }

            ChatRoomForTeamDto.ResponseForCreateChatRoomList chatRoomDto = chatRoomForTeamService.createOrGetChatRoom(teamBoard, author, participant);
            return ResponseEntity.ok(chatRoomDto);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("채팅방 생성에 실패했습니다.");
        }    }

    @Override
    public ResponseEntity<?> deleteChatRoom(String chatRoomId, User user) {
        try{
            chatRoomForTeamService.deleteChatRoom(chatRoomId);
            return ResponseEntity.ok("채팅방 삭제에 성공했습니다.");
        }catch (Exception e){
            return ResponseEntity.badRequest().body("채팅방 삭제에 실패했습니다.");
        }    }
}
