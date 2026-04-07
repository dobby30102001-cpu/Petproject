package com.example.examprepbackend.dto.response.questions;

import com.example.examprepbackend.dto.response.answer.AnswerPublicResponse;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@JsonPropertyOrder({"id", "content", "answers"})
public class QuestionPublicResponse {
    private Integer id;

    private String content;

    private List<AnswerPublicResponse> answers;
}
