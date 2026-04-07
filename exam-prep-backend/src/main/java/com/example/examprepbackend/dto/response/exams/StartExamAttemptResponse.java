package com.example.examprepbackend.dto.response.exams;


import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StartExamAttemptResponse {
    private Integer attemptId;
    private Integer examId;
    private String examTitle;
    private String status;
    private LocalDateTime startTime;
}
