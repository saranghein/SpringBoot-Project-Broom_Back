package com.kwhackathon.broom.date_tag.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kwhackathon.broom.date_tag.dto.DateTagRequest.TrainingDateDto;
import com.kwhackathon.broom.date_tag.dto.DateTagResponse.TrainingDateList;
import com.kwhackathon.broom.date_tag.entity.DateTag;
import com.kwhackathon.broom.date_tag.repository.DateTagRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DateTagServiceImpl implements DateTagService {
    private final DateTagRepository dateTagRepository;

    @Override
    @Transactional
    public void createDateTag(TrainingDateDto trainingDateDto) {
        dateTagRepository.save(trainingDateDto.toEntity());
    }

    @Override
    public TrainingDateList getAllDateTag() {
        return new TrainingDateList(dateTagRepository.findAllByOrderByTrainingDateAsc());
    }

    @Override
    @Transactional
    public void updateDateTag(Long tagId, TrainingDateDto trainingDateDto) {
        DateTag tag = dateTagRepository.findById(tagId).orElseThrow(() -> new NullPointerException());
        tag.updateTrainingDate(trainingDateDto.getTrainingDate());
    }

    @Override
    @Transactional
    public void deleteDateTag(Long tagId) {
        dateTagRepository.deleteById(tagId);
    }
    
}
