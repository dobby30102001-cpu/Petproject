package com.example.examprepbackend.dto.response.clazz;

import jakarta.persistence.Column;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class ClassResponse {
    private Integer id;

    private String name;

    private LocalDateTime createDate;

    private Long studentCount;

    private Long teacherCount;

}
