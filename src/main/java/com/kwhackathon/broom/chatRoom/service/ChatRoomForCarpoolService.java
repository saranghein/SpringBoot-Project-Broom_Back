package com.kwhackathon.broom.chatRoom.service;

import com.kwhackathon.broom.carpool.entity.CarpoolBoard;
import com.kwhackathon.broom.chatMessage.dto.ChatMessageForCarpoolDto;
import com.kwhackathon.broom.chatMessage.entity.ChatMessageForCarpool;
import com.kwhackathon.broom.chatMessage.repository.ChatMessageForCarpoolRepository;
import com.kwhackathon.broom.chatRoom.dto.ChatRoomForCarpoolDto;
import com.kwhackathon.broom.chatRoom.entity.ChatRoomForCarpool;
import com.kwhackathon.broom.chatRoom.repository.ChatRoomForCarpoolRepository;
import com.kwhackathon.broom.user.entity.User;
import com.kwhackathon.broom.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ChatRoomForCarpoolService {
    private final ChatRoomForCarpoolRepository chatRoomForCarpoolRepository;
    private final ChatMessageForCarpoolRepository chatMessageForCarpoolRepository;
    private final UserService userService;

    // 채팅방 목록 조회
    public List<ChatRoomForCarpoolDto.ResponseForGetChatRoomList> getChatRoomList(User participant) {

        return chatRoomForCarpoolRepository.findByAuthorOrParticipant(participant, participant).stream()
                .map(chatRoom -> ChatRoomForCarpoolDto.ResponseForGetChatRoomList.fromEntity(chatRoom, participant))
                .collect(Collectors.toList());
    }

    public ChatRoomForCarpool findByChatRoomId(String chatRoomId) {
        return chatRoomForCarpoolRepository.findById(chatRoomId)
                .orElseThrow(() -> new IllegalArgumentException("채팅방을 찾을 수 없습니다: " + chatRoomId));
    }

    public ChatRoomForCarpoolDto.ResponseForGetChatRoomList getChatRoom(Optional<CarpoolBoard> carpoolBoard, User author, User participant) {
        if (participant == null) {
            throw new IllegalArgumentException("Participant cannot be null.");
        }
//        return chatRoomForCarpoolRepository.findByCarpoolBoardAndAuthorAndParticipant(carpoolBoard, author, participant)
//                .orElse(null);
        Optional<ChatRoomForCarpool> room = chatRoomForCarpoolRepository.findByCarpoolBoardAndAuthorAndParticipant(carpoolBoard, author, participant);
        return room
                .map(chatRoom -> ChatRoomForCarpoolDto.ResponseForGetChatRoomList.fromEntity(chatRoom, participant))
                .orElse(null);

    }


    // 채팅방 생성 또는 기존 방 ID 반환
    @Transactional
    public ChatRoomForCarpoolDto.ResponseForCreateChatRoomList createOrGetChatRoom(Optional<CarpoolBoard> carpoolBoard, User author, User participant) {
        Optional<ChatRoomForCarpool> existingRoom = chatRoomForCarpoolRepository.findByCarpoolBoardAndAuthorAndParticipant(carpoolBoard, author, participant);

        return existingRoom
                .map(chatRoom -> ChatRoomForCarpoolDto.ResponseForCreateChatRoomList.fromEntity(chatRoom, participant))
                .orElseGet(() -> {
                    ChatRoomForCarpool newRoom = ChatRoomForCarpoolDto.ResponseForCreateChatRoomList.toEntity(author, participant, carpoolBoard);
                    chatRoomForCarpoolRepository.save(newRoom);
                    return ChatRoomForCarpoolDto.ResponseForCreateChatRoomList.fromEntity(newRoom, participant);
                });

    }
    @Transactional
    public void deleteChatRoom(String chatRoomId) {
        chatMessageForCarpoolRepository.deleteByChatRoomId(chatRoomId);

        chatRoomForCarpoolRepository.deleteById(chatRoomId);
    }

}
