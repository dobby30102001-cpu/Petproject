package com.example.examprepbackend.dto.response.teacher;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ScoreDistribution {
    private String range;

    private Long count;
}
