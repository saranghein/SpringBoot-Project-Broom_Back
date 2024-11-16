package com.kwhackathon.broom.carpool.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import com.kwhackathon.broom.carpool.dto.request.IsFullCheckDto;
import com.kwhackathon.broom.carpool.dto.request.WriteCarpoolBoardDto;
import com.kwhackathon.broom.carpool.service.CarpoolBoardService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class CarpoolBoardController implements CarpoolBoardOperations {
    private final CarpoolBoardService carpoolBoardService;

    @Override
    public ResponseEntity<?> getAllBoards() {
        return ResponseEntity.status(HttpStatus.OK).body(carpoolBoardService.getAllBoards());
    }

    @Override
    public ResponseEntity<?> searchBoard(String category, String keyword) {
        return ResponseEntity.status(HttpStatus.OK).body(carpoolBoardService.searchCarpoolBoard(category, keyword));
    }

    @Override
    public ResponseEntity<?> viewBoardDetail(Long carpoolBoardId) {
        try {
            return ResponseEntity.status(HttpStatus.OK).body(carpoolBoardService.getSingleCarpoolBoard(carpoolBoardId));
        } catch (NullPointerException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @Override
    public ResponseEntity<?> getRecruitingBoard() {
        return ResponseEntity.status(HttpStatus.OK).body(carpoolBoardService.getRecruitingBoard());
    }

    @Override
    public ResponseEntity<?> createBoard(WriteCarpoolBoardDto writeCarpoolBoardDto) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(carpoolBoardService.createCarpoolBoard(writeCarpoolBoardDto));
        } catch (NullPointerException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("게시물을 작성하는 중에 오류가 발생했습니다");
        }
        // return ResponseEntity.status(HttpStatus.CREATED).body("게시물 작성이 완료되었습니다");
    }

    @Override
    public ResponseEntity<?> getIntialEditInfo(Long carpoolBoardId) {
        try {
            return ResponseEntity.status(HttpStatus.OK).body(
                    carpoolBoardService.getPreviousCarpoolBoard(carpoolBoardId));
        } catch (NullPointerException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        }
    }

    @Override
    public ResponseEntity<?> editBoard(Long carpoolBoardId, WriteCarpoolBoardDto writeCarpoolBoardDto) {
        try {
            return ResponseEntity.status(HttpStatus.OK)
                    .body(carpoolBoardService.updateCarpoolBoard(carpoolBoardId, writeCarpoolBoardDto));
        } catch (NullPointerException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @Override
    public ResponseEntity<?> deleteBoard(Long carpoolBoardId) {
        carpoolBoardService.deleteCarpoolBoard(carpoolBoardId);
        return ResponseEntity.status(HttpStatus.OK).body("게시물이 삭제되었습니다");
    }

    @Override
    public ResponseEntity<?> getMyBoard() {
        try {
            return ResponseEntity.status(HttpStatus.OK).body(carpoolBoardService.getMyBoards());
        } catch (NullPointerException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @Override
    public ResponseEntity<?> checkIsFull(Long carpoolBoardId, IsFullCheckDto isFullCheckDto) {
        try {
            carpoolBoardService.carpoolBoardIsFullCheck(carpoolBoardId, isFullCheckDto);
        } catch (NullPointerException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
        return ResponseEntity.status(HttpStatus.OK).body("게시물의 상태가 변경되었습니다");
    }
}
