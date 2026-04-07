package com.example.examprepbackend.dto.response.exams;

import com.example.examprepbackend.constant.ExamType;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class AttemptReviewDetailResponse {
    private Integer attemptId;
    private Integer examId;
    private String examTitle;
    private ExamType examType;
    private Boolean reviewAllowed;
    private LocalDateTime submittedAt;
    private List<ReviewQuestionResponse> questions;
}
