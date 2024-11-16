package com.kwhackathon.broom.carpool.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import com.kwhackathon.broom.carpool.dto.request.IsFullCheckDto;
import com.kwhackathon.broom.carpool.dto.request.WriteCarpoolBoardDto;

public interface CarpoolBoardOperations {
    @GetMapping("/view/carpool")
    ResponseEntity<?> getAllBoards();

    @GetMapping(value = "/view/carpool", params = { "category", "keyword" })
    ResponseEntity<?> searchBoard(@RequestParam String category, @RequestParam String keyword);

    @GetMapping("/view/carpool/{carpoolBoardId}")
    ResponseEntity<?> viewBoardDetail(@PathVariable Long carpoolBoardId);

    @GetMapping("/view/carpool/recruiting")
    ResponseEntity<?> getRecruitingBoard();

    @PostMapping("/carpool")
    ResponseEntity<?> createBoard(@RequestBody WriteCarpoolBoardDto writeCarpoolBoardDto);

    @GetMapping("/carpool/edit/{carpoolBoardId}")
    ResponseEntity<?> getIntialEditInfo(@PathVariable Long carpoolBoardId);

    @PutMapping("/carpool/edit/{carpoolBoardId}")
    ResponseEntity<?> editBoard(@PathVariable Long carpoolBoardId,
            @RequestBody WriteCarpoolBoardDto writeCarpoolBoardDto);

    @DeleteMapping("/carpool/{carpoolBoardId}")
    ResponseEntity<?> deleteBoard(@PathVariable Long carpoolBoardId);

    @GetMapping("/mypage/carpool")
    ResponseEntity<?> getMyBoard();

    @PutMapping("/carpool/check/{carpoolBoardId}")
    ResponseEntity<?> checkIsFull(@PathVariable Long carpoolBoardId, @RequestBody IsFullCheckDto isFullCheckDto);
}
