package com.example.examprepbackend.dto.response.questions;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AttemptQuestionOptionResponse {
    private Integer optionId;
    private String optionLabel;
    private String optionContent;
}
