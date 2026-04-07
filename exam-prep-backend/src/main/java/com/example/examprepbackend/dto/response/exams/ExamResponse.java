package com.example.examprepbackend.dto.response.exams;

import com.example.examprepbackend.constant.ExamType;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.LocalTime;

@Getter
@Setter
@JsonPropertyOrder({"id", "code", "title", "duration", "category", "createDate", "questions", "attempts"})
public class ExamResponse {

    private Integer id;

    private String code;

    private String title;

    private LocalTime duration;

    private String category;

    private LocalDateTime createDate;

    private Long questions;

    private Long attempts;

    private String creatorName;

    private Boolean isActive;

    private ExamType examType;

    private Boolean reviewAllowed;

    private Double passScore;

}
