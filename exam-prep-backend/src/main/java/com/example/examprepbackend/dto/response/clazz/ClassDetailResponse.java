package com.example.examprepbackend.dto.response.clazz;

import com.example.examprepbackend.dto.response.exams.ExamSummaryResponse;
import com.example.examprepbackend.entity.Exam;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class ClassDetailResponse {
    private Integer id;

    private String name;

    private LocalDateTime createDate;

    private Long studentCount;

    private List<ExamSummaryResponse> exams;
}
