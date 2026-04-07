package com.example.examprepbackend.dto.response.exams;

import com.example.examprepbackend.constant.ExamType;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubmitExamAttemptResponse {

    private Integer attemptId;

    private Integer examId;

    private String examTitle;

    private ExamType examType;

    private Double score;

    private Double passScore;

    private Boolean passed;

    private String resultStatus;

    private Integer totalQuestions;

    private Integer correctCount;

    private Integer wrongCount;

    private Integer blankCount;

    private Integer timeSpentSeconds;

    private Boolean reviewAllowed;

    private LocalDateTime endTime;

    private String message;
}
