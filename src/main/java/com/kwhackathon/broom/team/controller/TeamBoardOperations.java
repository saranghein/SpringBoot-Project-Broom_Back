package com.kwhackathon.broom.team.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import com.kwhackathon.broom.team.dto.request.IsFullCheckDto;
import com.kwhackathon.broom.team.dto.request.WriteTeamBoardDto;

public interface TeamBoardOperations {
    @GetMapping("/view/team")
    ResponseEntity<?> getAllBoards();

    @GetMapping(value = "/view/team", params = { "category", "keyword" })
    ResponseEntity<?> searchBoard(@RequestParam String category, @RequestParam String keyword);

    @GetMapping("/view/team/{teamBoardId}")
    ResponseEntity<?> viewBoardDetail(@PathVariable Long teamBoardId);

    @GetMapping("/view/team/recruiting")
    ResponseEntity<?> getRecruitingBoard();

    @PostMapping("/team")
    ResponseEntity<?> createBoard(@RequestBody WriteTeamBoardDto writeTeamBoardDto);

    @GetMapping("/team/edit/{teamBoardId}")
    ResponseEntity<?> getIntialEditInfo(@PathVariable Long teamBoardId);

    @PutMapping("/team/edit/{teamBoardId}")
    ResponseEntity<?> editBoard(@PathVariable Long teamBoardId,
            @RequestBody WriteTeamBoardDto writeTeamBoardDto);

    @DeleteMapping("/team/{teamBoardId}")
    ResponseEntity<?> deleteBoard(@PathVariable Long teamBoardId);

    @GetMapping("/mypage/team")
    ResponseEntity<?> getMyBoard();

    @PutMapping("/team/check/{teamBoardId}")
    ResponseEntity<?> checkIsFull(@PathVariable Long teamBoardId, @RequestBody IsFullCheckDto isFullCheckDto);
}
