package com.example.examprepbackend.dto.request.exams;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ExamTypeRequest {

    @NotNull(message = "Exam type not null")
    @NotBlank(message = "Exam type not blank")
    private String examType;

}
