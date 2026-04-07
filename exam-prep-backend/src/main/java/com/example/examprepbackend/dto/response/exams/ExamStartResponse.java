package com.example.examprepbackend.dto.response.exams;

import com.example.examprepbackend.dto.response.questions.QuestionPublicResponse;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Getter
@Setter
@JsonPropertyOrder({"attemptId", "examCode", "examTitle", "duration", "startTime", "questions"})
public class ExamStartResponse {

    private Integer attemptId;

    private String examCode;

    private String examTitle;

    private LocalTime duration;

    private String examType;

    private LocalDateTime startTime;

    private List<QuestionPublicResponse> questions;

}
