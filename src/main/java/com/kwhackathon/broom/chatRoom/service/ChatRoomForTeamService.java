package com.kwhackathon.broom.chatRoom.service;

import com.kwhackathon.broom.chatMessage.repository.ChatMessageForTeamRepository;
import com.kwhackathon.broom.chatRoom.dto.ChatRoomForTeamDto;
import com.kwhackathon.broom.chatRoom.entity.ChatRoomForTeam;
import com.kwhackathon.broom.chatRoom.repository.ChatRoomForTeamRepository;
import com.kwhackathon.broom.team.entity.TeamBoard;
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
public class ChatRoomForTeamService {
    private final ChatRoomForTeamRepository chatRoomForTeamRepository;
    private final ChatMessageForTeamRepository chatMessageForTeamRepository;

    // 채팅방 목록 조회
    public List<ChatRoomForTeamDto.ResponseForGetChatRoomList> getChatRoomList(User participant) {

        return chatRoomForTeamRepository.findByAuthorOrParticipant(participant, participant).stream()
                .map(chatRoom -> ChatRoomForTeamDto.ResponseForGetChatRoomList.fromEntity(chatRoom, participant))
                .collect(Collectors.toList());
    }

    public ChatRoomForTeam findByChatRoomId(String chatRoomId) {
        return chatRoomForTeamRepository.findById(chatRoomId)
                .orElseThrow(() -> new IllegalArgumentException("채팅방을 찾을 수 없습니다: " + chatRoomId));
    }

    public ChatRoomForTeamDto.ResponseForGetChatRoomList getChatRoom(Optional<TeamBoard> teamBoard, User author, User participant) {
        if (participant == null) {
            throw new IllegalArgumentException("Participant cannot be null.");
        }
        Optional<ChatRoomForTeam> room = chatRoomForTeamRepository.findByTeamBoardAndAuthorAndParticipant(teamBoard, author, participant);
        return room
                .map(chatRoom -> ChatRoomForTeamDto.ResponseForGetChatRoomList.fromEntity(chatRoom, participant))
                .orElseThrow(() -> new NullPointerException("채팅방을 찾을 수 없습니다"));

    }

    // 채팅방 생성 또는 기존 방 ID 반환
    @Transactional
    public ChatRoomForTeamDto.ResponseForCreateChatRoomList createOrGetChatRoom(Optional<TeamBoard> teamBoard, User author, User participant) {
        Optional<ChatRoomForTeam> existingRoom = chatRoomForTeamRepository.findByTeamBoardAndAuthorAndParticipant(teamBoard, author, participant);

        return existingRoom
                .map(chatRoom -> ChatRoomForTeamDto.ResponseForCreateChatRoomList.fromEntity(chatRoom))
                .orElseGet(() -> {
                    ChatRoomForTeam newRoom = ChatRoomForTeamDto.ResponseForCreateChatRoomList.toEntity(author, participant, teamBoard);
                    chatRoomForTeamRepository.save(newRoom);
                    return ChatRoomForTeamDto.ResponseForCreateChatRoomList.fromEntity(newRoom);
                });

    }

    @Transactional
    public void deleteChatRoom(String chatRoomId) {
        chatMessageForTeamRepository.deleteByChatRoomId(chatRoomId);

        chatRoomForTeamRepository.deleteById(chatRoomId);
    }
}
