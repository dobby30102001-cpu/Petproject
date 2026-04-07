package com.example.examprepbackend.dto.response.teacher;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DashboardStats {

    private long totalExams;

    private long totalQuestions;

    private long totalStudents;
}