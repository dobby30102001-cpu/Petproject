package com.example.examprepbackend.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "favorite_exam")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FavorviteExam {
    @EmbeddedId
    private FavoriteExamId id;

    @ManyToOne
    @MapsId("examId")
    @JoinColumn(name = "exam_id")
    private Exam exam;

    @ManyToOne
    @MapsId("studentId")
    @JoinColumn(name = "student_id")
    private Users student;
}
