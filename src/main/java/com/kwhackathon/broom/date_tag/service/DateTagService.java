package com.kwhackathon.broom.date_tag.service;

import com.kwhackathon.broom.date_tag.dto.DateTagRequest.TrainingDateDto;
import com.kwhackathon.broom.date_tag.dto.DateTagResponse.TrainingDateElement;
import com.kwhackathon.broom.date_tag.dto.DateTagResponse.TrainingDateList;

public interface DateTagService {
    TrainingDateElement createDateTag(TrainingDateDto trainingDateDto);

    TrainingDateList getAllDateTag();

    void updateDateTag(Long tagId, TrainingDateDto trainingDateDto);

    void deleteDateTag(Long tagId);
}
