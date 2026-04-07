package com.example.examprepbackend.dto.response.teacher;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class QuestionCountResponse {

    Long countTotal;

    Long countEasy;

    Long countMedium;

    Long countHard;
}
