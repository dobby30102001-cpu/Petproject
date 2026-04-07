package com.example.examprepbackend.repository;

import com.example.examprepbackend.constant.AttemptStatus;
import com.example.examprepbackend.constant.ExamType;
import com.example.examprepbackend.dto.response.teacher.ScoreDistribution;
import com.example.examprepbackend.entity.Exam;
import com.example.examprepbackend.entity.ExamAttempt;
import com.example.examprepbackend.entity.Users;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ExamAttemptRepository extends JpaRepository<ExamAttempt, Integer> {

    Long countByExam_Id(Integer exam_id);

    Optional<ExamAttempt> findByIdAndStudentUsername(Integer id, String username);

    Page<ExamAttempt> findByExam(Exam exam, Pageable pageable);

    Page<ExamAttempt> findByExamIn(List<Exam> examList, Pageable pageable);

    Page<ExamAttempt> findByStudent(Users student, Pageable pageable);

//    ExamAttempt findByExamAndStatus(Exam exam, AttemptStatus status);

    boolean existsByExamAndStudentAndStatus(Integer exam, Integer studentId, AttemptStatus status);

    ExamAttempt findByExamAndStudentAndStatus(Exam exam, Users student, AttemptStatus status);

    Page<ExamAttempt> findByStudentAndExamExamType(Users user, ExamType examType, Pageable pageable);
    @Query("""
                SELECT new com.example.examprepbackend.dto.response.teacher.ScoreDistribution(
                    CASE 
                        WHEN e.score >= 0 AND e.score < 4 THEN '0-4'
                               WHEN e.score >= 4 AND e.score < 5 THEN '4-5'
                               WHEN e.score >= 5 AND e.score < 6 THEN '5-6'
                               WHEN e.score >= 6 AND e.score < 7 THEN '6-7'
                               WHEN e.score >= 7 AND e.score < 8 THEN '7-8'
                               WHEN e.score >= 8 AND e.score < 9 THEN '8-9'
                               WHEN e.score >= 9 AND e.score <= 10 THEN '9-10'
                    END,
                    COUNT(e)
                )
                FROM ExamAttempt e
                WHERE e.status = 'SUBMITTED'
                GROUP BY 
                    CASE 
                               WHEN e.score >= 0 AND e.score < 4 THEN '0-4'
                                  WHEN e.score >= 4 AND e.score < 5 THEN '4-5'
                                  WHEN e.score >= 5 AND e.score < 6 THEN '5-6'
                                  WHEN e.score >= 6 AND e.score < 7 THEN '6-7'
                                  WHEN e.score >= 7 AND e.score < 8 THEN '7-8'
                                  WHEN e.score >= 8 AND e.score < 9 THEN '8-9'
                                  WHEN e.score >= 9 AND e.score <= 10 THEN '9-10'
                    END
                ORDER BY 1
            """)
    List<ScoreDistribution> getScoreDistribution();
}
