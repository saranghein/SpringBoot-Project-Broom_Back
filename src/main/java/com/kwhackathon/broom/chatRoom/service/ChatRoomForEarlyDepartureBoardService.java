package com.kwhackathon.broom.chatRoom.service;

import com.kwhackathon.broom.carpool.entity.CarpoolBoard;
import com.kwhackathon.broom.chatMessage.repository.ChatMessageForEarlyDepartureRepository;
import com.kwhackathon.broom.chatRoom.dto.ChatRoomForCarpoolDto;
import com.kwhackathon.broom.chatRoom.dto.ChatRoomForEarlyDepartureDto;
import com.kwhackathon.broom.chatRoom.entity.ChatRoomForCarpool;
import com.kwhackathon.broom.chatRoom.entity.ChatRoomForEarlyDeparture;
import com.kwhackathon.broom.chatRoom.repository.ChatRoomForEarlyDepartureRepository;
import com.kwhackathon.broom.earlyDepartureBoard.entity.EarlyDepartureBoard;
import com.kwhackathon.broom.user.entity.User;
import com.kwhackathon.broom.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ChatRoomForEarlyDepartureBoardService {
    private final ChatRoomForEarlyDepartureRepository chatRoomForEarlyDepartureRepository;
    private final ChatMessageForEarlyDepartureRepository chatMessageForEarlyDepartureRepository;
    private final UserService userService;

    // 채팅방 목록 조회
    public List<ChatRoomForEarlyDepartureDto.ResponseForGetChatRoomList> getChatRoomList(User participant) {

        return chatRoomForEarlyDepartureRepository.findByAuthorOrParticipant(participant, participant).stream()
                .map(chatRoom -> ChatRoomForEarlyDepartureDto.ResponseForGetChatRoomList.fromEntity(chatRoom, participant))
                .collect(Collectors.toList());
    }

    public ChatRoomForEarlyDeparture findByChatRoomId(String chatRoomId) {
        return chatRoomForEarlyDepartureRepository.findById(chatRoomId)
                .orElseThrow(() -> new IllegalArgumentException("채팅방을 찾을 수 없습니다: " + chatRoomId));
    }

    public ChatRoomForEarlyDepartureDto.ResponseForGetChatRoomList getChatRoom(Optional<EarlyDepartureBoard> earlyDepartureBoard, User author, User participant) {
        if (participant == null) {
            throw new IllegalArgumentException("Participant cannot be null.");
        }
//        return chatRoomForCarpoolRepository.findByCarpoolBoardAndAuthorAndParticipant(carpoolBoard, author, participant)
//                .orElse(null);
        Optional<ChatRoomForEarlyDeparture> room = chatRoomForEarlyDepartureRepository.findByEarlyDepartureBoardAndAuthorAndParticipant(earlyDepartureBoard, author, participant);
        return room
                .map(chatRoom -> ChatRoomForEarlyDepartureDto.ResponseForGetChatRoomList.fromEntity(chatRoom, participant))
                .orElse(null);

    }


    // 채팅방 생성 또는 기존 방 ID 반환
    @Transactional
    public ChatRoomForEarlyDepartureDto.ResponseForCreateChatRoomList createOrGetChatRoom(Optional<EarlyDepartureBoard> earlyDepartureBoard, User author, User participant) {
        Optional<ChatRoomForEarlyDeparture> existingRoom = chatRoomForEarlyDepartureRepository.findByEarlyDepartureBoardAndAuthorAndParticipant(earlyDepartureBoard, author, participant);

        return existingRoom
                .map(chatRoom -> ChatRoomForEarlyDepartureDto.ResponseForCreateChatRoomList.fromEntity(chatRoom, participant))
                .orElseGet(() -> {
                    ChatRoomForEarlyDeparture newRoom = ChatRoomForEarlyDepartureDto.ResponseForCreateChatRoomList.toEntity(author, participant, earlyDepartureBoard);
                    chatRoomForEarlyDepartureRepository.save(newRoom);
                    return ChatRoomForEarlyDepartureDto.ResponseForCreateChatRoomList.fromEntity(newRoom, participant);
                });

    }
    @Transactional
    public void deleteChatRoom(String chatRoomId) {
        chatMessageForEarlyDepartureRepository.deleteByChatRoomId(chatRoomId);

        chatRoomForEarlyDepartureRepository.deleteById(chatRoomId);
    }
}
