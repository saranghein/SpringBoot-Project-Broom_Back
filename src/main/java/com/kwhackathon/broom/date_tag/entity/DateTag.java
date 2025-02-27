package com.kwhackathon.broom.date_tag.entity;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "training_date")
public class DateTag {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "date_tag_id", nullable = false, unique = true)
    Long dateTagId;
    
    @Column(name = "training_date", nullable = false)
    LocalDate trainingDate;

    public void updateTrainingDate(LocalDate trainingDate){
        this.trainingDate = trainingDate;
    }
}
