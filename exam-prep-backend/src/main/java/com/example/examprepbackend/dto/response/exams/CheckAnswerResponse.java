package com.example.examprepbackend.dto.response.exams;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Builder
@Getter
@Setter
public class CheckAnswerResponse {
    private boolean isCorrect;
    private Integer correctAnswerId;
    private String explanation;
}
