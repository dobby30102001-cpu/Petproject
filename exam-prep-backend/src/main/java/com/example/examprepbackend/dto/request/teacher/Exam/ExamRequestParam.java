package com.example.examprepbackend.dto.request.teacher.Exam;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;

@Getter
@Setter
public class ExamRequestParam {
    private String code;

    private String title;

    private String categoryName;

    private LocalDate minDate;

    private LocalDate maxDate;
}
