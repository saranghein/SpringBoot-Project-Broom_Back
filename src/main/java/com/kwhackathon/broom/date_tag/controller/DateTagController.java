package com.kwhackathon.broom.date_tag.controller;

import org.springframework.http.ResponseEntity;

import com.kwhackathon.broom.date_tag.dto.DateTagRequest.TrainingDateDto;

public interface DateTagController {
    ResponseEntity<?> getServiceStatus();

    ResponseEntity<?> createTrainingDateTag(TrainingDateDto trainingDateDto);
    
    ResponseEntity<?> updateTrainingDateTag(Long tagId, TrainingDateDto trainingDateDto);

    ResponseEntity<?> deleteTrainingDateTag(Long tagId);

    ResponseEntity<?> getAllDateTags();
}