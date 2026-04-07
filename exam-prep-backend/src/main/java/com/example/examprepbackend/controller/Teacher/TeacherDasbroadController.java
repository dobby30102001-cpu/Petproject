package com.example.examprepbackend.controller.Teacher;

import com.example.examprepbackend.common.BaseResponse;
import com.example.examprepbackend.dto.response.teacher.DashboardStats;
import com.example.examprepbackend.dto.response.teacher.ScoreDistribution;
import com.example.examprepbackend.service.ExamAttemptService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/teacher/dashboard")
@RequiredArgsConstructor
public class TeacherDasbroadController {
    private final ExamAttemptService  examAttemptService;

    @GetMapping("/score-distribution")
    public ResponseEntity<BaseResponse<List<ScoreDistribution>>> getScoreDistribution() {
        return ResponseEntity.ok(new BaseResponse<>(examAttemptService.getScoreDistribution(),"Gen All Score successfull "));
    }

    @GetMapping("/stats")
    public ResponseEntity<BaseResponse<DashboardStats>> getStats() {
        return ResponseEntity.ok(new BaseResponse<>(examAttemptService.getStats(),"Gen All Stats successfull "));
    }
}
