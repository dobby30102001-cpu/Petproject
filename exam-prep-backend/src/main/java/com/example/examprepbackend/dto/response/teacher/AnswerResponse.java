package com.example.examprepbackend.dto.response.teacher;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AnswerResponse {

    private String content;

    private Boolean isCorrect;
}
