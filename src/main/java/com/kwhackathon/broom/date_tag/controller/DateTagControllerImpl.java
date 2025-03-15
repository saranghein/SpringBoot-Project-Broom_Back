package com.kwhackathon.broom.date_tag.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.kwhackathon.broom.date_tag.dto.DateTagRequest.TrainingDateDto;
import com.kwhackathon.broom.date_tag.service.DateTagService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class DateTagControllerImpl implements DateTagController {
    private final DateTagService dateTagService;

    @Override
    @PostMapping("/admin/date-tag")
    public ResponseEntity<?> createTrainingDateTag(@RequestBody TrainingDateDto trainingDateDto) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(dateTagService.createDateTag(trainingDateDto));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @Override
    @PutMapping("/admin/date-tag/{tagId}")
    public ResponseEntity<?> updateTrainingDateTag(@PathVariable("tagId") Long tagId, @RequestBody TrainingDateDto trainingDateDto) {
        try {
            dateTagService.updateDateTag(tagId, trainingDateDto);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
        return ResponseEntity.status(HttpStatus.OK).body("날짜 태그가 수정되었습니다.");
    }

    @Override
    @DeleteMapping("/admin/date-tag/{tagId}")
    public ResponseEntity<?> deleteTrainingDateTag(@PathVariable("tagId") Long tagId) {
        try {
            dateTagService.deleteDateTag(tagId);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
        return ResponseEntity.status(HttpStatus.OK).body("날짜 태그가 삭제되었습니다.");
    }

    @Override
    @GetMapping("/date-tag")
    public ResponseEntity<?> getAllDateTags() {
        try {
            return ResponseEntity.status(HttpStatus.OK).body(dateTagService.getAllDateTag());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}
