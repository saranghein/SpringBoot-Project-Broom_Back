package com.kwhackathon.broom.user.entity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.kwhackathon.broom.board.entity.Board;
import com.kwhackathon.broom.bookmark.entity.Bookmark;
import com.kwhackathon.broom.participant.entity.Participant;
import com.kwhackathon.broom.user.dto.UserRequest.ChangeUserInfoDto;
import com.kwhackathon.broom.user.util.MilitaryBranch;
import com.kwhackathon.broom.user.util.Role;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class User implements UserDetails {
    @Id
    @Column(name = "user_id", nullable = false)
    private String userId;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "nickname", nullable = false)
    private String nickname;

    @Column(name = "discharge_year", nullable = false)
    private int dischargeYear; // 전역 연도

    @Column(name = "military_branch", nullable = false)
    @Enumerated(EnumType.STRING)
    private MilitaryBranch militaryBranch; // 군구분

    @Column(name = "role", nullable = false)
    @Enumerated(EnumType.STRING)
    private Role role; // 권한 정보

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<Board> boards;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<Bookmark> bookmarks;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<Participant> participants;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Collection<GrantedAuthority> collection = new ArrayList<>();
        collection.add(new SimpleGrantedAuthority(role.toString()));
        return collection;
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.userId;
    }

    public void updateUserInfo(ChangeUserInfoDto dto) {
        this.nickname = dto.getNickname();
        this.dischargeYear = dto.getDischargeYear();
        this.militaryBranch = dto.getMilitaryBranch();
    }

    public void updatePassword(String newPassword) {
        this.password = newPassword;
    }
}
