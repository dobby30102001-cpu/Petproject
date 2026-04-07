package com.example.examprepbackend.service;

import com.example.examprepbackend.dto.request.exams.CheckAnswerRequest;
import com.example.examprepbackend.dto.request.teacher.Question.CreateQuestionRequest;
import com.example.examprepbackend.dto.request.teacher.Question.QuestionRequestParam;
import com.example.examprepbackend.dto.response.questions.QuestionPublicResponse;
import com.example.examprepbackend.dto.response.exams.CheckAnswerResponse;
import com.example.examprepbackend.dto.response.teacher.QuestionCountResponse;
import com.example.examprepbackend.dto.response.questions.QuestionResponse;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface QuestionService {

    //teacher

    Page<QuestionResponse> getAllQuestions(QuestionRequestParam Param, Pageable pageable);

    Page<QuestionResponse> getMyQuestions(QuestionRequestParam param, Pageable pageable);

    QuestionResponse getQuestionById(Integer id);

    List<QuestionResponse> getQuestionsByExamId(Integer examId);

    List<QuestionPublicResponse> getQuestionsPublicByExamId(Integer examId);

    QuestionResponse createQuestion(CreateQuestionRequest request);

    QuestionResponse updateQuestion(Integer id, CreateQuestionRequest request);

    void deleteQuestion(Integer id);

    void exportQuestionToExcel(HttpServletResponse response) throws IOException;

    void importQuestionFromExcel(MultipartFile file) throws IOException;

    QuestionCountResponse getAllQuestionsCount();

    // student
    Page<QuestionResponse> getAllQuestionsByStudent(Pageable pageable);

    CheckAnswerResponse checkAnswer(CheckAnswerRequest request);
}
