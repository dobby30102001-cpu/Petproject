package com.example.examprepbackend.dto.request.exams;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class SubmitExamAttemptRequest {
    private List<SubmitAnswerRequest> answers;
}
