package com.kwhackathon.broom.carpool.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.kwhackathon.broom.carpool.dto.request.IsFullCheckDto;
import com.kwhackathon.broom.carpool.dto.request.WriteCarpoolBoardDto;
import com.kwhackathon.broom.carpool.dto.response.Author;
import com.kwhackathon.broom.carpool.dto.response.BoardIdDto;
import com.kwhackathon.broom.carpool.dto.response.CarpoolBoardListDto;
import com.kwhackathon.broom.carpool.dto.response.CarpoolBoardListElement;
import com.kwhackathon.broom.carpool.dto.response.CarpoolBoardPreviousInfoDto;
import com.kwhackathon.broom.carpool.dto.response.SingleCarpoolBoardDto;
import com.kwhackathon.broom.carpool.entity.CarpoolBoard;
import com.kwhackathon.broom.carpool.repository.CarpoolBoardRepository;
import com.kwhackathon.broom.user.entity.User;
import com.kwhackathon.broom.user.repository.UserRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class CarpoolBoardService {
        private final CarpoolBoardRepository carpoolBoardRepository;
        private final UserRepository userRepository;

        public BoardIdDto createCarpoolBoard(WriteCarpoolBoardDto dto) {
                String userId = SecurityContextHolder.getContext().getAuthentication().getName();
                User user = userRepository.findByUserId(userId)
                                .orElseThrow(() -> new NullPointerException("올바른 회원 정보가 아닙니다"));
                CarpoolBoard carpoolBoard = carpoolBoardRepository.save(CarpoolBoard.builder()
                                .title(dto.getTitle())
                                .content(dto.getContent())
                                .departPlace(dto.getDepartPlace())
                                .departTime(dto.getDepartTime())
                                .personnel(dto.getPersonnel())
                                .price(dto.getPrice())
                                .trainingDate(dto.getTrainingDate())
                                .user(user)
                                .build());
                return new BoardIdDto(carpoolBoard.getCarpoolBoardId());
        }

        public CarpoolBoardListDto getAllBoards() {
                List<CarpoolBoardListElement> elements = new ArrayList<>();
                elements = carpoolBoardRepository.findAllOrderByCreatedAtDesc().stream()
                                .map(carpoolBoard -> {
                                        LocalDateTime createdAt = carpoolBoard.getCreatedAt();
                                        String createdAtStr = createdAt
                                                        .format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));

                                        if (carpoolBoard.getCreatedAt().getYear() == LocalDate.now().getYear()) {
                                                createdAtStr = createdAt.format(DateTimeFormatter.ofPattern("MM/dd"));
                                        }
                                        if (createdAt.toLocalDate().compareTo(LocalDate.now()) == 0) {
                                                createdAtStr = createdAt.getHour() + ":" + createdAt.getMinute();
                                        }
                                        return new CarpoolBoardListElement(
                                                        carpoolBoard.getCarpoolBoardId(),
                                                        carpoolBoard.getTitle(),
                                                        createdAtStr,
                                                        carpoolBoard.getTrainingDate()
                                                                        .format(DateTimeFormatter.ofPattern(
                                                                                        "MM/dd")),
                                                        carpoolBoard.getDepartPlace(),
                                                        carpoolBoard.getDepartTime().format(DateTimeFormatter.ofPattern(
                                                                        "HH:mm")),
                                                        carpoolBoard.isFull());
                                })
                                .collect(Collectors.toList());

                return new CarpoolBoardListDto(elements);
        }

        // 제목(title), 훈련 날짜(trainingDate), 출발 장소(departPlace?)
        public CarpoolBoardListDto searchCarpoolBoard(String category, String keyword) {
                List<CarpoolBoardListElement> elements = new ArrayList<>();
                if (category.equals("title")) {
                        elements = carpoolBoardRepository.findByTitleContainingOrderByCreatedAtDesc(keyword).stream()
                                        .map(carpoolBoard -> {
                                                LocalDateTime createdAt = carpoolBoard.getCreatedAt();
                                                String createdAtStr = createdAt.format(
                                                                DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm"));
                                                if (createdAt.toLocalDate().compareTo(LocalDate.now()) == 0) {
                                                        createdAtStr = createdAt.getHour() + ":"
                                                                        + createdAt.getMinute();
                                                }
                                                if (carpoolBoard.getCreatedAt().getYear() == LocalDate.now()
                                                                .getYear()) {
                                                        createdAtStr = createdAt
                                                                        .format(DateTimeFormatter.ofPattern("MM/dd"));
                                                }
                                                return new CarpoolBoardListElement(
                                                                carpoolBoard.getCarpoolBoardId(),
                                                                carpoolBoard.getTitle(),
                                                                createdAtStr,
                                                                carpoolBoard.getTrainingDate()
                                                                                .format(DateTimeFormatter.ofPattern(
                                                                                                "MM/dd")),
                                                                carpoolBoard.getDepartPlace(),
                                                                carpoolBoard.getDepartTime()
                                                                                .format(DateTimeFormatter.ofPattern(
                                                                                                "HH:mm")),
                                                                carpoolBoard.isFull());
                                        })
                                        .collect(Collectors.toList());
                }
                if (category.equals("trainingDate")) {
                        elements = carpoolBoardRepository.findByTrainingDateOrderByCreatedAtDesc(LocalDate.parse(keyword)).stream()
                                        .map(carpoolBoard -> {
                                                LocalDateTime createdAt = carpoolBoard.getCreatedAt();
                                                String createdAtStr = createdAt.format(
                                                                DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm"));
                                                if (createdAt.toLocalDate().compareTo(LocalDate.now()) == 0) {
                                                        createdAtStr = createdAt.getHour() + ":"
                                                                        + createdAt.getMinute();
                                                }
                                                if (carpoolBoard.getCreatedAt().getYear() == LocalDate.now()
                                                                .getYear()) {
                                                        createdAtStr = createdAt
                                                                        .format(DateTimeFormatter.ofPattern("MM/dd"));
                                                }
                                                return new CarpoolBoardListElement(
                                                                carpoolBoard.getCarpoolBoardId(),
                                                                carpoolBoard.getTitle(),
                                                                createdAtStr,
                                                                carpoolBoard.getTrainingDate()
                                                                                .format(DateTimeFormatter.ofPattern(
                                                                                                "MM/dd")),
                                                                carpoolBoard.getDepartPlace(),
                                                                carpoolBoard.getDepartTime()
                                                                                .format(DateTimeFormatter.ofPattern(
                                                                                                "HH:mm")),
                                                                carpoolBoard.isFull());
                                        })
                                        .collect(Collectors.toList());
                }
                if (category.equals("departPlace")) {
                        elements = carpoolBoardRepository.findByDepartPlaceContainingOrderByCreatedAtDesc(keyword).stream()
                                        .map(carpoolBoard -> {
                                                LocalDateTime createdAt = carpoolBoard.getCreatedAt();
                                                String createdAtStr = createdAt.format(
                                                                DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm"));
                                                if (createdAt.toLocalDate().compareTo(LocalDate.now()) == 0) {
                                                        createdAtStr = createdAt.getHour() + ":"
                                                                        + createdAt.getMinute();
                                                }
                                                if (carpoolBoard.getCreatedAt().getYear() == LocalDate.now()
                                                                .getYear()) {
                                                        createdAtStr = createdAt
                                                                        .format(DateTimeFormatter.ofPattern("MM/dd"));
                                                }
                                                return new CarpoolBoardListElement(
                                                                carpoolBoard.getCarpoolBoardId(),
                                                                carpoolBoard.getTitle(),
                                                                createdAtStr,
                                                                carpoolBoard.getTrainingDate()
                                                                                .format(DateTimeFormatter.ofPattern(
                                                                                                "MM/dd")),
                                                                carpoolBoard.getDepartPlace(),
                                                                carpoolBoard.getDepartTime()
                                                                                .format(DateTimeFormatter.ofPattern(
                                                                                                "HH:mm")),
                                                                carpoolBoard.isFull());
                                        })
                                        .collect(Collectors.toList());
                }
                return new CarpoolBoardListDto(elements);
        }

        public CarpoolBoardListDto getRecruitingBoard() {
                List<CarpoolBoardListElement> elements = new ArrayList<>();
                elements = carpoolBoardRepository.findByIsFullOrderByCreatedAtDesc(false).stream()
                                .map(carpoolBoard -> {
                                        LocalDateTime createdAt = carpoolBoard.getCreatedAt();
                                        String createdAtStr = createdAt
                                                        .format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));

                                        if (carpoolBoard.getCreatedAt().getYear() == LocalDate.now().getYear()) {
                                                createdAtStr = createdAt.format(DateTimeFormatter.ofPattern("MM/dd"));
                                        }
                                        if (createdAt.toLocalDate().compareTo(LocalDate.now()) == 0) {
                                                createdAtStr = createdAt.getHour() + ":" + createdAt.getMinute();
                                        }
                                        return new CarpoolBoardListElement(
                                                        carpoolBoard.getCarpoolBoardId(),
                                                        carpoolBoard.getTitle(),
                                                        createdAtStr,
                                                        carpoolBoard.getTrainingDate()
                                                                        .format(DateTimeFormatter.ofPattern(
                                                                                        "MM/dd")),
                                                        carpoolBoard.getDepartPlace(),
                                                        carpoolBoard.getDepartTime().format(DateTimeFormatter.ofPattern(
                                                                        "HH:mm")),
                                                        carpoolBoard.isFull());
                                })
                                .collect(Collectors.toList());

                return new CarpoolBoardListDto(elements);
        }

        public SingleCarpoolBoardDto getSingleCarpoolBoard(Long carpoolBoardId) {
                CarpoolBoard carpoolBoard = carpoolBoardRepository.findById(carpoolBoardId)
                                .orElseThrow(() -> new NullPointerException("게시물을 찾을 수 없습니다"));
                User writer = userRepository.findByUserId(carpoolBoard.getUser().getUserId())
                                .orElseThrow(() -> new NullPointerException("해당 게시물의 작성자가 유효하지 않습니다"));

                int reserveYear = LocalDate.now().getYear() - writer.getDischargeYear();
                Author author = new Author(writer.getUserId(), writer.getNickname(), reserveYear,
                                writer.getMilitaryChaplain());

                LocalDateTime createdAt = carpoolBoard.getCreatedAt();
                String createdAtStr = createdAt.format(DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm"));
                if (carpoolBoard.getCreatedAt().toLocalDate().compareTo(LocalDate.now()) == 0) {
                        createdAtStr = createdAt.getHour() + ":" + createdAt.getMinute();
                }
                return new SingleCarpoolBoardDto(carpoolBoard.getCarpoolBoardId(),
                                author,
                                carpoolBoard.getTitle(),
                                createdAtStr,
                                carpoolBoard.getTrainingDate().format(DateTimeFormatter.ofPattern(
                                                "MM/dd")),
                                carpoolBoard.getDepartPlace(),
                                carpoolBoard.getDepartTime().format(DateTimeFormatter.ofPattern(
                                                "HH:mm")),
                                carpoolBoard.getPersonnel(),
                                carpoolBoard.getPrice(),
                                carpoolBoard.getContent(),
                                carpoolBoard.isFull());
        }

        public CarpoolBoardPreviousInfoDto getPreviousCarpoolBoard(Long carpoolBoardId) {
                String userId = SecurityContextHolder.getContext().getAuthentication().getName();
                CarpoolBoard carpoolBoard = carpoolBoardRepository.findById(carpoolBoardId)
                                .orElseThrow(() -> new NullPointerException("게시물을 찾을 수 없습니다"));
                User writer = userRepository.findByUserId(carpoolBoard.getUser().getUserId())
                                .orElseThrow(() -> new NullPointerException("해당 게시물의 작성자가 유효하지 않습니다"));

                if (!userId.equals(writer.getUserId())) {
                        throw new IllegalArgumentException("해당 게시물의 수정권한이 없습니다");
                }

                return new CarpoolBoardPreviousInfoDto(carpoolBoard.getCarpoolBoardId(),
                                carpoolBoard.getTitle(),
                                carpoolBoard.getContent(),
                                carpoolBoard.getTrainingDate(),
                                carpoolBoard.getDepartPlace(),
                                carpoolBoard.getDepartTime().format(DateTimeFormatter.ofPattern(
                                                "HH:mm")),
                                carpoolBoard.getPersonnel(),
                                carpoolBoard.getPrice());
        }

        public BoardIdDto updateCarpoolBoard(Long carpoolBoardId, WriteCarpoolBoardDto dto) {
                CarpoolBoard carpoolBoard = carpoolBoardRepository.findById(carpoolBoardId)
                                .orElseThrow(() -> new NullPointerException("게시물을 찾을 수 없습니다"));
                userRepository.findByUserId(carpoolBoard.getUser().getUserId())
                                .orElseThrow(() -> new NullPointerException("해당 게시물의 작성자가 유효하지 않습니다"));

                carpoolBoard.updateCarpoolBoard(dto);

                return new BoardIdDto(carpoolBoard.getCarpoolBoardId());
        }

        public void deleteCarpoolBoard(Long carpoolBoardId) {
                carpoolBoardRepository.deleteById(carpoolBoardId);
        }

        public CarpoolBoardListDto getMyBoards() {
                String userId = SecurityContextHolder.getContext().getAuthentication().getName();
                User user = userRepository.findByUserId(userId)
                                .orElseThrow(() -> new NullPointerException("올바른 회원 정보가 아닙니다"));
                List<CarpoolBoardListElement> elements = new ArrayList<>();

                elements = user.getCarpoolBoards().stream()
                                .map(carpoolBoard -> {
                                        LocalDateTime createdAt = carpoolBoard.getCreatedAt();
                                        String createdAtStr = createdAt
                                                        .format(DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm"));
                                        if (createdAt.toLocalDate().compareTo(LocalDate.now()) == 0) {
                                                createdAtStr = createdAt.getHour() + ":" + createdAt.getMinute();
                                        }
                                        return new CarpoolBoardListElement(
                                                        carpoolBoard.getCarpoolBoardId(),
                                                        carpoolBoard.getTitle(),
                                                        createdAtStr,
                                                        carpoolBoard.getTrainingDate()
                                                                        .format(DateTimeFormatter.ofPattern(
                                                                                        "MM/dd")),
                                                        carpoolBoard.getDepartPlace(),
                                                        carpoolBoard.getDepartTime().format(DateTimeFormatter.ofPattern(
                                                                        "HH:mm")),
                                                        carpoolBoard.isFull());
                                })
                                .collect(Collectors.toList());

                return new CarpoolBoardListDto(elements);
        }

        public void carpoolBoardIsFullCheck(Long carpoolBoardId, IsFullCheckDto dto) {
                CarpoolBoard carpoolBoard = carpoolBoardRepository.findById(carpoolBoardId)
                                .orElseThrow(() -> new NullPointerException("게시물을 찾을 수 없습니다"));
                carpoolBoard.updateIsFull(dto);
        }
    public Optional<CarpoolBoard> getCarpoolBoard(Long carpoolBoardId) {
        return carpoolBoardRepository.findById(carpoolBoardId);
    }
}
