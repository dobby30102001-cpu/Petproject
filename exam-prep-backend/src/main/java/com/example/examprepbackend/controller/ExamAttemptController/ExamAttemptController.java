package com.example.examprepbackend.controller.ExamAttemptController;

import com.example.examprepbackend.common.BaseResponse;
import com.example.examprepbackend.constant.ExamType;
import com.example.examprepbackend.dto.request.exams.CheckAnswerRequest;
import com.example.examprepbackend.dto.request.exams.ExamTypeRequest;
import com.example.examprepbackend.dto.request.exams.SubmitExamAttemptRequest;
import com.example.examprepbackend.dto.response.exams.*;
import com.example.examprepbackend.service.ExamAttemptService;
import com.example.examprepbackend.service.QuestionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/exams-attempt")
@RequiredArgsConstructor
public class ExamAttemptController {

    private final ExamAttemptService examAttemptService;
    private final QuestionService questionService;

//    @PostMapping("/{examId}/attempts")
//    public ResponseEntity<StartExamAttemptResponse> startAttempt(@PathVariable Integer examId) {
//        StartExamAttemptResponse response = examAttemptService.startAttempt(examId);
//        return ResponseEntity.status(HttpStatus.CREATED).body(response);
//    }
//    @GetMapping("/attempts/{attemptId}/questions")
//    public ResponseEntity<AttemptQuestionsFullResponse> getAttemptQuestions(@PathVariable Integer attemptId) {
//        AttemptQuestionsFullResponse response = examAttemptService.getAttemptQuestions(attemptId);
//        return ResponseEntity.ok(response);
//    }

    @PostMapping("/exam-id/{id}/start")
    // Bắt đầu một lần làm bài mới cho user trên một đề thi
    public ResponseEntity<BaseResponse<ExamStartResponse>> startExam(@PathVariable Integer id,
                                                                     Authentication authentication) {
        return ResponseEntity.ok().body(new BaseResponse<>(examAttemptService.startExam(id, authentication), "Start exam"));
    }

    //Tai lai bai thi
    @PostMapping("/exam-id/{id}/restart")
    public ResponseEntity<BaseResponse<ExamStartResponse>> restartExam(@PathVariable Integer id,
                                                                       Authentication authentication) {
        return ResponseEntity.ok().body(new BaseResponse<>(examAttemptService.restartExam(id, authentication), "Restart exam"));
    }

    // Nộp bài, chấm điểm, khóa bài, lưu kết quả chính thức.
    @PostMapping("/attempts/{attemptId}/submit")
    public ResponseEntity<SubmitExamAttemptResponse> submitAttempt(
            @PathVariable Integer attemptId,
            @RequestBody SubmitExamAttemptRequest request
    ) {
        SubmitExamAttemptResponse response = examAttemptService.submitAttempt(attemptId, request);
        return ResponseEntity.ok(response);
    }

    //Check dap an cho thi thu
    @PostMapping("/check-answer")
    public ResponseEntity<BaseResponse<CheckAnswerResponse>> checkAnswer(@RequestBody CheckAnswerRequest request) {
        CheckAnswerResponse responseData = questionService.checkAnswer(request);
        return ResponseEntity.ok(BaseResponse.success(responseData));
    }

    //Thi that
    @GetMapping("/attempts/{attemptId}/result")
    public ResponseEntity<AttemptResultResponse> getAttemptResult(@PathVariable Integer attemptId) {
        AttemptResultResponse response = examAttemptService.getAttemptResult(attemptId);
        return ResponseEntity.ok(response);
    }

    //Luyen tap
    @GetMapping("/attempts/{attemptId}/review-detail")
    public ResponseEntity<AttemptReviewDetailResponse> getAttemptReviewDetail(@PathVariable Integer attemptId) {
        AttemptReviewDetailResponse response = examAttemptService.getAttemptReviewDetail(attemptId);
        return ResponseEntity.ok(response);
    }

    //Lay danh sach ket qua thi
    @GetMapping("/attempts/exam-type")
    public ResponseEntity<BaseResponse<Page<ExamAttemptResponse>>> getAttemptsByExamType(Authentication authentication,
                                                                                         @PageableDefault(size = 5, sort = "id", direction = Sort.Direction.DESC)
                                                                                         Pageable pageable,
                                                                                         @RequestParam String examType) {
        Page<ExamAttemptResponse> data = examAttemptService.getAttemptsByExamType(authentication, pageable, examType);
        return ResponseEntity.ok(new BaseResponse<>(data, "Get All Exams Successfully"));
    }


}


