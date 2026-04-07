package com.example.examprepbackend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
// day la bang trung gian nhe

public class FavoriteExamId implements Serializable {
    @Column(name = "exam_id")
    private Integer examId;

    @Column(name = "student_id")
    private Integer studentId;
}
