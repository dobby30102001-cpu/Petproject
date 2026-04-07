package com.example.examprepbackend.dto.response.answer;

import com.example.examprepbackend.entity.Question;
import jakarta.persistence.Column;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AnswerSummaryResponse {
    private Integer id;

    private String content;

    private Boolean isCorrect;

}
