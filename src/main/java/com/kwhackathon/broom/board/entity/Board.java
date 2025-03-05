package com.kwhackathon.broom.board.entity;

import java.util.List;
import java.util.UUID;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import com.kwhackathon.broom.participant.entity.Participant;
import org.hibernate.annotations.CreationTimestamp;

import com.kwhackathon.broom.board.dto.BoardRequest.WriteBoardDto;
import com.kwhackathon.broom.bookmark.entity.Bookmark;
import com.kwhackathon.broom.user.entity.User;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "board")
public class Board {
    @Id
    @Builder.Default
    @Column(name = "board_id", nullable = false, unique = true)
    private String boardId = UUID.randomUUID().toString();

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "content", nullable = false)
    private String content;

    @Column(name = "place", nullable = false)
    private String place;

    @Column(name = "time", nullable = false)
    private LocalTime time;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "personnel", nullable = false)
    private int personnel;

    @Column(name = "training_date", nullable = false)
    private LocalDate trainingDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "board", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<Bookmark> bookmarks;

    @OneToMany(mappedBy = "board", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<Participant> participants;
    public void updateBoard(WriteBoardDto writeBoardDto) {
        this.title = writeBoardDto.getTitle();
        this.content = writeBoardDto.getContent();
        this.place = writeBoardDto.getPlace();
        this.time = writeBoardDto.getTime();
        this.personnel = writeBoardDto.getPersonnel();
        this.trainingDate = writeBoardDto.getTrainingDate();
    }
}
