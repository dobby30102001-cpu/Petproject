package com.example.examprepbackend.dto.request.clazz;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
public class ClassRequestParam {

    private String name;

    private LocalDate minDate;

    private LocalDate maxDate;

}
