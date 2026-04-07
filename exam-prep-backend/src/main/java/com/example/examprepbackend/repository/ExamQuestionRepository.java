package com.example.examprepbackend.repository;

import com.example.examprepbackend.entity.ExamQuestion;
import com.example.examprepbackend.entity.ExamQuestionId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ExamQuestionRepository extends JpaRepository<ExamQuestion, ExamQuestionId> {
    Long countByExam_Id(Integer examId);

    List<ExamQuestion> findByExam_Id(Integer examId);

    @Query("select eq.question.id from ExamQuestion eq where eq.exam.id = :examId")
    List<Integer> findQuestionsByExamId(@Param("examId") Integer examId);

    @Modifying
    @Query("delete from ExamQuestion eq where eq.exam.id = :examId")
    void deleteByExam_Id(@Param("examId") Integer examId);
}
