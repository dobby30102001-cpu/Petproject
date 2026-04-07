package com.example.examprepbackend.dto.request.exams;

import lombok.Data;

@Data
public class CheckAnswerRequest {

    private Integer questionId;

    private Integer selectedAnswerId;
}
