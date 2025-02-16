package com.kwhackathon.broom.board.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import com.kwhackathon.broom.board.dto.BoardRequest.WriteBoardDto;
import com.kwhackathon.broom.board.service.BoardService;

import jakarta.validation.ConstraintViolationException;
import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequiredArgsConstructor
public class BoardControllerImpl implements BoardController {
    private final BoardService boardService;

    @Override
    @PostMapping("/board")
    public ResponseEntity<?> createBoard(@RequestBody WriteBoardDto writeBoardDto) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(boardService.createBoard(writeBoardDto));
        } catch (ConstraintViolationException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("오류가 발생하였습니다.");
        }
    }

    @Override
    @GetMapping("/board/view/all/{page}")
    public ResponseEntity<?> getAllBoards(@PathVariable("page") int page, @RequestParam("recruiting") boolean recruiting) {
        try {
            return ResponseEntity.status(HttpStatus.OK).body(boardService.getAllBoard(page, recruiting));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @Override
    @GetMapping("/board/view/{boardId}")
    public ResponseEntity<?> getSingleBoardDetail(@PathVariable("boardId") String boardId) {
        try {
            return ResponseEntity.status(HttpStatus.OK).body(boardService.getSingleBoardDetail(boardId));
        } catch (NullPointerException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("오류가 발생하였습니다.");
        }
    }

    @Override
    @GetMapping("/board/search/{page}")
    public ResponseEntity<?> searchBoard(@PathVariable("page") int page, @RequestParam("type") String type,
            @RequestParam("keyword") String keyword, @RequestParam("recruiting") boolean recruiting) {
        try {
            return ResponseEntity.status(HttpStatus.OK).body(boardService.searchBoard(page, type, keyword, 
                    recruiting));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @Override
    @PatchMapping("/board/{boardId}")
    public ResponseEntity<?> editBoard(@PathVariable("boardId") String boardId, @RequestBody WriteBoardDto writeBoardDto) {
        try {
            boardService.updateBoard(boardId, writeBoardDto);
        } catch (NullPointerException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (ConstraintViolationException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("오류가 발생하였습니다.");
        }
        return ResponseEntity.status(HttpStatus.OK).body("수정이 완료되었습니다.");
    }

    @Override
    @DeleteMapping("/board/{boardId}")
    public ResponseEntity<?> deleteBoard(@PathVariable("boardId") String boardId) {
        try {
            boardService.deleteBoard(boardId);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("오류가 발생하였습니다.");
        }
        return ResponseEntity.status(HttpStatus.OK).body("게시물이 삭제되였습니다.");
    }

    @Override
    @GetMapping("/mypage/board/{page}")
    public ResponseEntity<?> getMyBoard(@PathVariable("page") int page) {
        try {
            return ResponseEntity.status(HttpStatus.OK).body(boardService.getMyBoard(page));
        } catch (NullPointerException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("오류가 발생하였습니다.");
        }
    }
}
