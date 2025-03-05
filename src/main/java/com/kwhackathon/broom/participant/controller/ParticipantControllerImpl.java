package com.kwhackathon.broom.participant.controller;

import com.kwhackathon.broom.participant.dto.ParticipantRequest;
import com.kwhackathon.broom.participant.dto.ParticipantResponse;
import com.kwhackathon.broom.participant.service.ParticipantService;
import com.kwhackathon.broom.user.entity.User;
import com.kwhackathon.broom.user.service.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/chat/list")
@AllArgsConstructor
@Tag(name = "ParticipantController", description = "ParticipantController API 목록")
public class ParticipantControllerImpl implements ParticipantController {

    private final ParticipantService participantService;
    private final UserService userService;

    // 해당 채팅방의 참여자 목록 조회
    @Override
    @GetMapping("/participant/{boardId}")
    public ResponseEntity<?> getParticipantsByBoardId(@PathVariable String boardId,@RequestParam(defaultValue = "0") int page) {

        try{
            return ResponseEntity.status(HttpStatus.OK)
                    .body(participantService.getParticipantsByBoardId(boardId));
        }catch (NullPointerException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)//404
                    .body("채팅방 목록 조회에 실패했습니다.");
        } catch(AccessDeniedException e){
            return ResponseEntity.status(HttpStatus.FORBIDDEN)//403
                    .body("채팅방 참여자만 조회할 수 있습니다.");
        } catch(Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("조회에 실패했습니다.");
        }
    }

    // 해당 유저의 채팅방들 조회
    @Override
    @GetMapping("")
    public ResponseEntity<?> getBoardByUserId(
            @AuthenticationPrincipal User user,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            // 서비스에서 채팅방 목록 조회
            ParticipantResponse.ChatRoomList chatRooms = participantService.getChatRoomListByUser(user, page,size);
            return ResponseEntity.ok(chatRooms);
        }catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST) //400
                    .body("잘못된 요청입니다.");
        }
    }

    // CASE1: 해당 채팅방의 참여자가 채팅방 나가기
    // CASE2: 사용자가 들어가서 강퇴되었다는 걸 확인하고 버튼을 눌렀다면
    @Override
    @DeleteMapping("/exit/{boardId}")
    public ResponseEntity<?> deleteParticipantByBoardIdAndUserId(@PathVariable String boardId, @AuthenticationPrincipal User user){
        try{
            // 방장이라면
            if(participantService.isAuthor(boardId,user.getUserId())){
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body("방장은 채팅방을 나갈 수 없습니다.");
            }

            boolean isDeleted=participantService.deleteParticipantByBoardIdAndUserId(boardId, user.getUserId());
            // 삭제 되었다면
            if (isDeleted) {
                return ResponseEntity.status(HttpStatus.OK)// 200 OK
                        .body("채팅방 삭제에 성공했습니다.");
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("채팅방에서 해당 사용자를 찾을 수 없습니다.");
            }
        }catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("채팅방 나가기에 실패했습니다.");
        }
    }

    // 강퇴 버튼을 누르면(강퇴 요청)
    @Override
    @PatchMapping("/expell/request")
    public ResponseEntity<?> expellRequire(@RequestBody ParticipantRequest.ExpellRequestInfo requestBody, @AuthenticationPrincipal User user) {
        try{
            String boardId = requestBody.getBoardId();

            // 방장이 아니면 강퇴불가
            if(!participantService.isAuthor(boardId,user.getUserId())){
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body("방장이 아닙니다.");
            }

            // 강퇴여부 추가
            String userId= userService.loadUserByUsername(requestBody.getExpellId()).getUserId();
            participantService.addExpellUserByBoardId(userId,boardId);
            return ResponseEntity.status(HttpStatus.OK)
                    .body("강퇴 요청에 성공했습니다.");

        }catch(Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("잘못된 요청입니다.");
        }
    }

}
