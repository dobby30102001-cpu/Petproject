package com.example.examprepbackend.dto.response.teacher;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Getter
@Setter
@JsonPropertyOrder({"id","code","title","duration","category","createDate","questions","attempts"})
public class ExamResponse {

    private Integer id;

    private String code;

    private String title;

    private LocalTime duration;

    private String category;

    private LocalDateTime createDate;

    private Long questions;

    private Long attempts;

    private String creatorName;


}
