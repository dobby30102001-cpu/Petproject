package com.example.examprepbackend.dto.response.exams;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class ReviewQuestionResponse {
    private Integer questionId;
    private Integer questionOrder;
    private String questionContent;
    private Integer selectedOptionId;
    private Integer correctOptionId;
    private Boolean isCorrect;
    private String explanation;
    private List<ReviewOptionResponse> options;
}
