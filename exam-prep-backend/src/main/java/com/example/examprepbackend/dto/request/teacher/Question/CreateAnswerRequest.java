package com.example.examprepbackend.dto.request.teacher.Question;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateAnswerRequest {
    private String content;
    private Boolean isCorrect;

}
