package com.example.examprepbackend.dto.response.questions;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class AttemptQuestionResponse {
    private Integer questionId;
    private Integer questionOrder;
    private String questionContent;
    private List<AttemptQuestionOptionResponse> options;
}
