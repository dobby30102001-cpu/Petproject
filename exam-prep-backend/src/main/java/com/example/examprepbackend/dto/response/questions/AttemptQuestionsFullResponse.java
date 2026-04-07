package com.example.examprepbackend.dto.response.questions;


import com.example.examprepbackend.constant.AttemptStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class AttemptQuestionsFullResponse {
    private Integer attemptId;
    private Integer examId;
    private String examTitle;
    private Integer durationMinutes;
    private AttemptStatus status;
    private LocalDateTime startTime;
    private List<AttemptQuestionResponse> questions;
}
