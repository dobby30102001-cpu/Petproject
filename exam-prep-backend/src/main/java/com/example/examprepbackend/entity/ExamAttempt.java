package com.example.examprepbackend.entity;


import com.example.examprepbackend.constant.AttemptStatus;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "exam_attempt")
public class    ExamAttempt {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    // đề thi được làm
    @ManyToOne
    @JoinColumn(name = "exam_id")
    private Exam exam;

    // user làm bài
    @ManyToOne
    @JoinColumn(name = "student_id")
    private Users student;


    @Column(name = "start_time")
    private LocalDateTime startTime;

    @Column(name = "end_time")
    private LocalDateTime endTime;

    private Double score;

    @Column(name = "blank_count")
    private Integer blankCount=0;

    @Column(name = "time_spent_seconds")
    private Integer timeSpentSeconds=0;


    @Column(name = "correct_count")
    private Integer correctCount=0;

    @Column(name = "wrong_count")
    private Integer wrongCount=0;

    @Enumerated(EnumType.STRING)
    private AttemptStatus status;


}
