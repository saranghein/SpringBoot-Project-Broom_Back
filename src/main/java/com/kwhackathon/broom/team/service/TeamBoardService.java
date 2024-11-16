package com.kwhackathon.broom.team.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.kwhackathon.broom.team.dto.response.BoardIdDto;
import com.kwhackathon.broom.team.dto.request.IsFullCheckDto;
import com.kwhackathon.broom.team.dto.request.WriteTeamBoardDto;
import com.kwhackathon.broom.team.dto.response.Author;
import com.kwhackathon.broom.team.dto.response.SingleTeamBoardDto;
import com.kwhackathon.broom.team.dto.response.TeamBoardListDto;
import com.kwhackathon.broom.team.dto.response.TeamBoardListElement;
import com.kwhackathon.broom.team.dto.response.TeamBoardPreviousInfoDto;
import com.kwhackathon.broom.team.entity.TeamBoard;
import com.kwhackathon.broom.team.repository.TeamBoardRepository;
import com.kwhackathon.broom.user.entity.User;
import com.kwhackathon.broom.user.repository.UserRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class TeamBoardService {
        private final TeamBoardRepository teamBoardRepository;
        private final UserRepository userRepository;

        // 아이디 넘겨주기
        public BoardIdDto createTeamBoard(WriteTeamBoardDto dto) {
                String userId = SecurityContextHolder.getContext().getAuthentication().getName();
                User user = userRepository.findByUserId(userId)
                                .orElseThrow(() -> new NullPointerException("올바른 회원 정보가 아닙니다"));
                TeamBoard teamBoard = teamBoardRepository.save(TeamBoard.builder()
                                .title(dto.getTitle())
                                .content(dto.getContent())
                                .meetingPlace(dto.getMeetingPlace())
                                .meetingTime(dto.getMeetingTime())
                                .personnel(dto.getPersonnel())
                                .trainingDate(dto.getTrainingDate())
                                .user(user)
                                .build());
                return new BoardIdDto(teamBoard.getTeamBoardId());
        }

        public TeamBoardListDto getAllBoards() {
                List<TeamBoardListElement> elements = new ArrayList<>();
                elements = teamBoardRepository.findAll().stream()
                                .map(teamBoard -> {
                                        LocalDateTime createdAt = teamBoard.getCreatedAt();
                                        String createdAtStr = createdAt
                                                        .format(DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm"));
                                        if (createdAt.toLocalDate().compareTo(LocalDate.now()) == 0) {
                                                createdAtStr = createdAt.getHour() + ":" + createdAt.getMinute();
                                        }
                                        if (teamBoard.getCreatedAt().getYear() == LocalDate.now().getYear()) {
                                                createdAtStr = createdAt.format(DateTimeFormatter.ofPattern("MM/dd"));
                                        }
                                        return new TeamBoardListElement(
                                                        teamBoard.getTeamBoardId(),
                                                        teamBoard.getTitle(),
                                                        createdAtStr,
                                                        teamBoard.getTrainingDate().format(DateTimeFormatter.ofPattern(
                                                                        "yyyy/MM/dd")),
                                                        teamBoard.getMeetingPlace(),
                                                        teamBoard.getMeetingTime().format(DateTimeFormatter.ofPattern(
                                                                        "HH:mm")),
                                                        teamBoard.isFull());
                                })
                                .collect(Collectors.toList());

                return new TeamBoardListDto(elements);
        }

        // 제목(title), 훈련 날짜(trainingDate)
        public TeamBoardListDto searchTeamBoard(String category, String keyword) {
                List<TeamBoardListElement> elements = new ArrayList<>();
                if (category.equals("title")) {
                        elements = teamBoardRepository.findByTitleContaining(keyword).stream()
                                        .map(teamBoard -> {
                                                LocalDateTime createdAt = teamBoard.getCreatedAt();
                                                String createdAtStr = createdAt.format(
                                                                DateTimeFormatter.ofPattern("yyyy/MM/dd"));

                                                if (teamBoard.getCreatedAt().getYear() == LocalDate.now().getYear()) {
                                                        createdAtStr = createdAt
                                                                        .format(DateTimeFormatter.ofPattern("MM/dd"));
                                                }
                                                if (createdAt.toLocalDate().compareTo(LocalDate.now()) == 0) {
                                                        createdAtStr = createdAt.getHour() + ":"
                                                                        + createdAt.getMinute();
                                                }
                                                return new TeamBoardListElement(
                                                                teamBoard.getTeamBoardId(),
                                                                teamBoard.getTitle(),
                                                                createdAtStr,
                                                                teamBoard.getTrainingDate()
                                                                                .format(DateTimeFormatter.ofPattern(
                                                                                                "yyyy/MM/dd")),
                                                                teamBoard.getMeetingPlace(),
                                                                teamBoard.getMeetingTime()
                                                                                .format(DateTimeFormatter.ofPattern(
                                                                                                "HH:mm")),
                                                                teamBoard.isFull());
                                        })
                                        .collect(Collectors.toList());
                }
                if (category.equals("trainingDate")) {
                        elements = teamBoardRepository.findByTrainingDate(LocalDate.parse(keyword)).stream()
                                        .map(teamBoard -> {
                                                LocalDateTime createdAt = teamBoard.getCreatedAt();
                                                String createdAtStr = createdAt.format(
                                                                DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm"));
                                                if (createdAt.toLocalDate().compareTo(LocalDate.now()) == 0) {
                                                        createdAtStr = createdAt.getHour() + ":"
                                                                        + createdAt.getMinute();
                                                }
                                                if (teamBoard.getCreatedAt().getYear() == LocalDate.now().getYear()) {
                                                        createdAtStr = createdAt
                                                                        .format(DateTimeFormatter.ofPattern("MM/dd"));
                                                }
                                                return new TeamBoardListElement(
                                                                teamBoard.getTeamBoardId(),
                                                                teamBoard.getTitle(),
                                                                createdAtStr,
                                                                teamBoard.getTrainingDate()
                                                                                .format(DateTimeFormatter.ofPattern(
                                                                                                "MM/dd")),
                                                                teamBoard.getMeetingPlace(),
                                                                teamBoard.getMeetingTime()
                                                                                .format(DateTimeFormatter.ofPattern(
                                                                                                "HH:mm")),
                                                                teamBoard.isFull());
                                        })
                                        .collect(Collectors.toList());
                }
                return new TeamBoardListDto(elements);
        }

        public SingleTeamBoardDto getSingleTeamBoard(Long teamBoardId) {
                TeamBoard teamBoard = teamBoardRepository.findById(teamBoardId)
                                .orElseThrow(() -> new NullPointerException("게시물을 찾을 수 없습니다"));
                User writer = userRepository.findByUserId(teamBoard.getUser().getUserId())
                                .orElseThrow(() -> new NullPointerException("해당 게시물의 작성자가 유효하지 않습니다"));

                int reserveYear = LocalDate.now().getYear() - writer.getDischargeYear();
                Author author = new Author(writer.getUserId(), writer.getNickname(), reserveYear,
                                writer.getMilitaryChaplain());

                LocalDateTime createdAt = teamBoard.getCreatedAt();
                String createdAtStr = createdAt.format(DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm"));
                if (teamBoard.getCreatedAt().toLocalDate().compareTo(LocalDate.now()) == 0) {
                        createdAtStr = createdAt.getHour() + ":" + createdAt.getMinute();
                }
                return new SingleTeamBoardDto(teamBoard.getTeamBoardId(),
                                author,
                                teamBoard.getTitle(),
                                createdAtStr,
                                teamBoard.getTrainingDate().format(DateTimeFormatter.ofPattern("MM/DD")),
                                teamBoard.getMeetingPlace(),
                                teamBoard.getMeetingTime().format(DateTimeFormatter.ofPattern(
                                                "HH:mm")),
                                teamBoard.getPersonnel(),
                                teamBoard.getContent(),
                                teamBoard.isFull());
        }
        public TeamBoardListDto getRecruitingBoard() {
                List<TeamBoardListElement> elements = new ArrayList<>();
                elements = teamBoardRepository.findByIsFull(false).stream()
                                .map(teamBoard -> {
                                        LocalDateTime createdAt = teamBoard.getCreatedAt();
                                        String createdAtStr = createdAt
                                                        .format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));

                                        if (teamBoard.getCreatedAt().getYear() == LocalDate.now().getYear()) {
                                                createdAtStr = createdAt.format(DateTimeFormatter.ofPattern("MM/dd"));
                                        }
                                        if (createdAt.toLocalDate().compareTo(LocalDate.now()) == 0) {
                                                createdAtStr = createdAt.getHour() + ":" + createdAt.getMinute();
                                        }
                                        return new TeamBoardListElement(
                                                        teamBoard.getTeamBoardId(),
                                                        teamBoard.getTitle(),
                                                        createdAtStr,
                                                        teamBoard.getTrainingDate()
                                                                        .format(DateTimeFormatter.ofPattern(
                                                                                        "MM/dd")),
                                                        teamBoard.getMeetingPlace(),
                                                        teamBoard.getMeetingTime()
                                                                        .format(DateTimeFormatter.ofPattern(
                                                                                        "HH:mm")),
                                                        teamBoard.isFull());
                                })
                                .collect(Collectors.toList());

                return new TeamBoardListDto(elements);
        }

        public TeamBoardPreviousInfoDto getPreviousTeamBoard(Long teamBoardId) {
                String userId = SecurityContextHolder.getContext().getAuthentication().getName();
                TeamBoard teamBoard = teamBoardRepository.findById(teamBoardId)
                                .orElseThrow(() -> new NullPointerException("게시물을 찾을 수 없습니다"));
                User writer = userRepository.findByUserId(teamBoard.getUser().getUserId())
                                .orElseThrow(() -> new NullPointerException("해당 게시물의 작성자가 유효하지 않습니다"));

                if (!userId.equals(writer.getUserId())) {
                        throw new IllegalArgumentException("해당 게시물의 수정권한이 없습니다");
                }

                return new TeamBoardPreviousInfoDto(teamBoard.getTeamBoardId(),
                                teamBoard.getTitle(),
                                teamBoard.getContent(),
                                teamBoard.getTrainingDate(),
                                teamBoard.getMeetingPlace(),
                                teamBoard.getMeetingTime().format(DateTimeFormatter.ofPattern(
                                                "HH:mm")),
                                teamBoard.getPersonnel());
        }

        // 아이디만 넘겨주는 걸로 변경
        public BoardIdDto updateTeamBoard(Long teamBoardId, WriteTeamBoardDto dto) {
                TeamBoard teamBoard = teamBoardRepository.findById(teamBoardId)
                                .orElseThrow(() -> new NullPointerException("게시물을 찾을 수 없습니다"));
                userRepository.findByUserId(teamBoard.getUser().getUserId())
                                .orElseThrow(() -> new NullPointerException("해당 게시물의 작성자가 유효하지 않습니다"));

                teamBoard.updateTeamBoard(dto);
                return new BoardIdDto(teamBoard.getTeamBoardId());
        }

        public void deleteTeamBoard(Long teamBoardId) {
                teamBoardRepository.deleteById(teamBoardId);
        }

        public TeamBoardListDto getMyBoards() {
                String userId = SecurityContextHolder.getContext().getAuthentication().getName();
                User user = userRepository.findByUserId(userId)
                                .orElseThrow(() -> new NullPointerException("올바른 회원 정보가 아닙니다"));
                List<TeamBoardListElement> elements = new ArrayList<>();

                elements = user.getTeamBoards().stream()
                                .map(teamBoard -> {
                                        LocalDateTime createdAt = teamBoard.getCreatedAt();
                                        String createdAtStr = createdAt
                                                        .format(DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm"));
                                        if (createdAt.toLocalDate().compareTo(LocalDate.now()) == 0) {
                                                createdAtStr = createdAt.getHour() + ":" + createdAt.getMinute();
                                        }
                                        return new TeamBoardListElement(
                                                        teamBoard.getTeamBoardId(),
                                                        teamBoard.getTitle(),
                                                        createdAtStr,
                                                        teamBoard.getTrainingDate().format(DateTimeFormatter.ofPattern(
                                                                        "MM/dd")),
                                                        teamBoard.getMeetingPlace(),
                                                        teamBoard.getMeetingTime().format(DateTimeFormatter.ofPattern(
                                                                        "HH:mm")),
                                                        teamBoard.isFull());
                                })
                                .collect(Collectors.toList());

                return new TeamBoardListDto(elements);
        }

        public void teamBoardIsFullCheck(Long teamBoardId, IsFullCheckDto dto) {
                TeamBoard teamBoard = teamBoardRepository.findById(teamBoardId)
                                .orElseThrow(() -> new NullPointerException("게시물을 찾을 수 없습니다"));
                teamBoard.updateIsFull(dto);
        }
    public Optional<TeamBoard> getTeamBoard(Long teamBoardId) {
        return teamBoardRepository.findById(teamBoardId);

    }
}
