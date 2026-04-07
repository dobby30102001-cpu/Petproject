package com.example.examprepbackend.dto.response.exams;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ReviewOptionResponse {
    private Integer optionId;
    private String optionLabel;
    private String optionContent;
    private Boolean isCorrect;
}
