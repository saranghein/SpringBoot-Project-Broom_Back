package com.kwhackathon.broom.team.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import com.kwhackathon.broom.team.dto.request.IsFullCheckDto;
import com.kwhackathon.broom.team.dto.request.WriteTeamBoardDto;
import com.kwhackathon.broom.team.service.TeamBoardService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class TeamBoardController implements TeamBoardOperations{
    private final TeamBoardService teamBoardService;

    @Override
    public ResponseEntity<?> getAllBoards() {
        return ResponseEntity.status(HttpStatus.OK).body(teamBoardService.getAllBoards());
    }

    @Override
    public ResponseEntity<?> searchBoard(String category, String keyword) {
        return ResponseEntity.status(HttpStatus.OK).body(teamBoardService.searchTeamBoard(category, keyword));
    }

    @Override
    public ResponseEntity<?> viewBoardDetail(Long teamBoardId) {
        try {
            return ResponseEntity.status(HttpStatus.OK).body(teamBoardService.getSingleTeamBoard(teamBoardId));
        } catch (NullPointerException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @Override
    public ResponseEntity<?> getRecruitingBoard() {
        return ResponseEntity.status(HttpStatus.OK).body(teamBoardService.getRecruitingBoard());
    }

    @Override
    public ResponseEntity<?> createBoard(WriteTeamBoardDto writeTeamBoardDto) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(teamBoardService.createTeamBoard(writeTeamBoardDto));
        } catch (NullPointerException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("게시물을 작성하는 중에 오류가 발생했습니다");
        }
        // return ResponseEntity.status(HttpStatus.CREATED).body("게시물 작성이 완료되었습니다");
    }

    @Override
    public ResponseEntity<?> getIntialEditInfo(Long teamBoardId) {
        try {
            return ResponseEntity.status(HttpStatus.OK).body(
                    teamBoardService.getPreviousTeamBoard(teamBoardId));
        } catch (NullPointerException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        }
    }

    @Override
    public ResponseEntity<?> editBoard(Long teamBoardId, WriteTeamBoardDto writeTeamBoardDto) {
        try {
            return ResponseEntity.status(HttpStatus.OK)
                    .body(teamBoardService.updateTeamBoard(teamBoardId, writeTeamBoardDto));
        } catch (NullPointerException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @Override
    public ResponseEntity<?> deleteBoard(Long teamBoardId) {
        teamBoardService.deleteTeamBoard(teamBoardId);
        return ResponseEntity.status(HttpStatus.OK).body("게시물이 삭제되었습니다");
    }

    @Override
    public ResponseEntity<?> getMyBoard() {
        try {
            return ResponseEntity.status(HttpStatus.OK).body(teamBoardService.getMyBoards());
        } catch (NullPointerException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @Override
    public ResponseEntity<?> checkIsFull(Long teamBoardId, IsFullCheckDto isFullCheckDto) {
        try {
            teamBoardService.teamBoardIsFullCheck(teamBoardId, isFullCheckDto);
        } catch (NullPointerException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
        return ResponseEntity.status(HttpStatus.OK).body("게시물의 상태가 변경되었습니다");
    }
}
