package com.example.examprepbackend.service;

import com.example.examprepbackend.dto.request.exams.ExamCreateRequest;
import com.example.examprepbackend.dto.request.exams.ExamRequestParam;
import com.example.examprepbackend.dto.request.exams.ExamUpdateRequest;
import com.example.examprepbackend.dto.response.exams.ExamAttemptResponse;
import com.example.examprepbackend.dto.response.exams.ExamResponse;
import com.example.examprepbackend.dto.response.exams.ExamSummaryResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;

import java.util.List;

public interface ExamService {
    Page<ExamResponse> getAllExams(ExamRequestParam examRequestParam, Pageable pageable);

    Page<ExamResponse> getExamsByTeacherName(Authentication authentication, ExamRequestParam examRequestParam, Pageable pageable);

    Page<ExamAttemptResponse> getExamAttemptsByTeacher(Authentication authentication, Pageable pageable);

    List<ExamSummaryResponse> getExamsByClassId(Integer classId);

    Page<ExamResponse> getPracticeExamsByClassId(Integer classId, ExamRequestParam examRequestParam, Pageable pageable);

    Page<ExamResponse> getOfficialExamsByClassId(Integer classId, ExamRequestParam examRequestParam, Pageable pageable);

    ExamSummaryResponse createExam(Authentication authentication, ExamCreateRequest examCreateRequest);

    ExamSummaryResponse updateExamById(Integer id, ExamUpdateRequest examUpdateRequest);

    Boolean deleteExamById(Integer id);

}
