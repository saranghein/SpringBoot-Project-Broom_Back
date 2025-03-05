package com.kwhackathon.broom.participant.entity;

import com.kwhackathon.broom.board.entity.Board;
import com.kwhackathon.broom.chat.entity.Chat;
import com.kwhackathon.broom.user.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "participant")
public class Participant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "participant_id", nullable = false)
    private Long id;

    @Column(name = "unread")
    private Long unread;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id")
    private Board board;

    // 강퇴 여부
    @Column(name="is_expelled")
    private Boolean isExpelled=false;

    @OneToMany(mappedBy = "participant", cascade = CascadeType.PERSIST)
    @ToString.Exclude // 순환 참조 방지
    private List<Chat> chats;

}
