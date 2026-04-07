package com.example.examprepbackend.dto.response.answer;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonPropertyOrder({"id", "label", "content"})
public class AnswerPublicResponse {
    private Integer id;

    private String label;

    private String content;
}
