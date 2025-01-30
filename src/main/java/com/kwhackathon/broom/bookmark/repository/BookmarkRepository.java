package com.kwhackathon.broom.bookmark.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;

import com.kwhackathon.broom.bookmark.entity.Bookmark;

public interface BookmarkRepository extends JpaRepository<Bookmark, Long> {

    boolean existsByUserUserIdAndBoardBoardId(String userId, String boardId);

    Slice<Bookmark> findSliceByUserUserIdOrderByBoardCreatedAtDesc(Pageable pageable, String userId);

    void deleteByUserUserIdAndBoardBoardId(String userId, String boardId);
}
