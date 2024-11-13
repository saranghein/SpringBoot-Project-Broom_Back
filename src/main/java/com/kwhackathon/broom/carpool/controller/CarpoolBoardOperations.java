package com.kwhackathon.broom.carpool.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.kwhackathon.broom.carpool.dto.request.WriteCarpoolBoardDto;

public interface CarpoolBoardOperations {
    @GetMapping("/carpool")
    ResponseEntity<?> getAllBoards();

    @GetMapping("/a")
    ResponseEntity<?> searchBoard();

    @GetMapping("/carpool/{carpoolBoardId}")
    ResponseEntity<?> viewBoardDetail(@PathVariable Long carpoolBoardId);

    @PostMapping("/carpool")
    ResponseEntity<?> createBoard(@RequestBody WriteCarpoolBoardDto writeCarpoolBoardDto);

    @GetMapping("/carpool/edit/{carpoolBoardId}")
    ResponseEntity<?> getIntialEditInfo(@PathVariable Long carpoolBoardId);

    @PutMapping("/carpool/edit/{carpoolBoardId}")
    ResponseEntity<?> editBoard(@PathVariable Long carpoolBoardId, @RequestBody WriteCarpoolBoardDto writeCarpoolBoardDto);

    @DeleteMapping("/carpool/{carpoolBoardId}")
    ResponseEntity<?> deleteBoard(@PathVariable Long carpoolBoardId);
}
