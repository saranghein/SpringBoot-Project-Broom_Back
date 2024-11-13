package com.kwhackathon.broom.user.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kwhackathon.broom.user.entity.User;

public interface UserRepository extends JpaRepository<User, String> {
    Optional<User> findByUserId(String UserId);

    boolean existsByUserId(String userId);

    boolean existsByNickname(String nickname);

    void deleteByUserId(String userId);
}
