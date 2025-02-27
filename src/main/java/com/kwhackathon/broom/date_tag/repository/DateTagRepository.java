package com.kwhackathon.broom.date_tag.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kwhackathon.broom.date_tag.entity.DateTag;

public interface DateTagRepository extends JpaRepository<DateTag, Long> {
    // 내림차순으로 날자 태그를 반환
    List<DateTag> findAllByOrderByTrainingDateAsc();
}
