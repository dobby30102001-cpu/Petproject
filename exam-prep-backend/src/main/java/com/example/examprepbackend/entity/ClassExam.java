package com.example.examprepbackend.entity;

import com.example.examprepbackend.constant.ClassExamStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "class_exam")
@Getter
@Setter
public class ClassExam {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "class_id", nullable = false)
    private Integer classId;

    @Column(name = "exam_id", nullable = false)
    private Integer examId;

    @Column(name = "attempt_count")
    private Integer attemptCount;

}