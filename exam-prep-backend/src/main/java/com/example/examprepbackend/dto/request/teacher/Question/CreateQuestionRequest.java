package com.example.examprepbackend.dto.request.teacher.Question;

import com.example.examprepbackend.constant.DifficultyLevel;
import com.example.examprepbackend.dto.response.teacher.AnswerResponse;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CreateQuestionRequest {

    private String content;

    private DifficultyLevel difficulty;

    private Integer categoryId;

    private List<CreateAnswerRequest> answers;

    private String explanation;
}
