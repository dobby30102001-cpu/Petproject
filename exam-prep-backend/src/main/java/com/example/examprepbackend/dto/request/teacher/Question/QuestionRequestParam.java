package com.example.examprepbackend.dto.request.teacher.Question;

import com.example.examprepbackend.constant.DifficultyLevel;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class QuestionRequestParam {

    private String content;

    private DifficultyLevel difficulty;

    private Integer categoryId;

    private Integer creatorId;

    private LocalDate minDate;

    private LocalDate maxDate;
}