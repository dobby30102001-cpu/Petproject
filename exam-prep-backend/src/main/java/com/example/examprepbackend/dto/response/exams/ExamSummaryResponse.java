package com.example.examprepbackend.dto.response.exams;

import com.example.examprepbackend.constant.ExamType;
import com.example.examprepbackend.entity.CategoryQuestion;
import com.example.examprepbackend.entity.Users;
import jakarta.persistence.Column;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.LocalTime;

@Getter
@Setter
public class ExamSummaryResponse {
    private Integer id;

    private String code;

    private String title;

    private LocalTime duration;

    private CategoryQuestion category;

    private LocalDateTime createDate;

    private ExamType examType;

}
