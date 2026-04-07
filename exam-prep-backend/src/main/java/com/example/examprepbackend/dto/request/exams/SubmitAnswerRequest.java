package com.example.examprepbackend.dto.request.exams;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SubmitAnswerRequest {
    private Integer questionId;
    private Integer selectedOptionId;
}
