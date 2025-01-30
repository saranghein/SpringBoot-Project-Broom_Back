package com.kwhackathon.broom.bookmark.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

public class BookmarkRequest {
    @Getter
    @NoArgsConstructor
    public static class CreateDto {
        private String boardId;
    }
}
