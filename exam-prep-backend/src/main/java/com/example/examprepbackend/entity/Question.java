package com.example.examprepbackend.entity;

import com.example.examprepbackend.constant.DifficultyLevel;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "question")
public class Question {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String content;

    @Enumerated(EnumType.STRING)
    @Column(name = "difficulty_level")
    private DifficultyLevel difficultyLevel;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private CategoryQuestion category;

    @ManyToOne
    @JoinColumn(name = "creator_id")
    private Users creator;

//    @ManyToOne
//    @JoinColumn(name = "exam_id")
//    private Exam exam;

//    @Column(name = "order_no")
//    private Integer orderNo;

    @Column(name = "create_date")
    private LocalDateTime createDate;

    private String explanation;
// thêm để xuất exccel
    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL)
    private List<Answer> answers;
}
