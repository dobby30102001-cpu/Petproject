package com.example.examprepbackend.dto.request.exams;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalTime;
import java.util.List;

@Getter
@Setter
public class ExamUpdateRequest {
    @NotNull(message = "Exam code not null")
    @NotBlank(message = "Exam code not blank")
    private String code;

    @NotNull(message = "Exam title not null")
    @NotBlank(message = "Exam title not blank")
    private String title;

    @NotNull(message = "Exam duration not null")
    private LocalTime duration;

    @NotNull(message = "Exam category not null")
    @NotBlank(message = "Exam category not blank")
    private String category;

    @NotNull(message = "Exam type not null")
    @NotBlank(message = "Exam type not blank")
    private String examType;

    @NotNull(message = "reviewAllowed not null")
    @NotBlank(message = "reviewAllowed not blank")
    private String reviewAllowed;

    @NotNull(message = "passScore not null")
    private Double passScore;

    private List<Integer> questionIds;
}
