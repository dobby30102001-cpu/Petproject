package com.example.examprepbackend.service.impl;

import com.example.examprepbackend.entity.Exam;
import com.example.examprepbackend.entity.ExamQuestion;
import com.example.examprepbackend.entity.ExamQuestionId;
import com.example.examprepbackend.entity.Question;
import com.example.examprepbackend.repository.ExamQuestionRepository;
import com.example.examprepbackend.service.ExamQuestionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExamQuestionServiceImpl implements ExamQuestionService {

    private final ExamQuestionRepository examQuestionRepository;

    @Override
    public void createExamQuestions(Exam exam, List<Question> questionList) {

        for (Question question : questionList) {
            ExamQuestion examQuestion = new ExamQuestion();

            ExamQuestionId examQuestionId = new ExamQuestionId();
            examQuestionId.setExamId(exam.getId());
            examQuestionId.setQuestionId(question.getId());

            examQuestion.setId(examQuestionId);
            examQuestion.setExam(exam);
            examQuestion.setQuestion(question);

            examQuestionRepository.save(examQuestion);
        }

    }
}
