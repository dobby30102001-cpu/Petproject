package com.example.examprepbackend.service;

import com.example.examprepbackend.entity.Exam;
import com.example.examprepbackend.entity.Question;

import java.util.List;

public interface ExamQuestionService {

    void createExamQuestions(Exam exam, List<Question> questionList);

}
