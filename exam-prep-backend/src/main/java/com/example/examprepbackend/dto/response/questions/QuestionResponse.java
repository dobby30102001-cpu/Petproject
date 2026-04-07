package com.example.examprepbackend.dto.response.questions;

import com.example.examprepbackend.constant.DifficultyLevel;
import com.example.examprepbackend.dto.response.teacher.AnswerResponse;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@JsonPropertyOrder({"id","content","difficulty","category","creator","createdDate","answers"})
public class QuestionResponse {

    private Integer id;

    private String content;

    private DifficultyLevel difficulty;

    private String category;

    private String creator;

    private String createdDate;

    private List<AnswerResponse> answers;

    private String explanation;
}