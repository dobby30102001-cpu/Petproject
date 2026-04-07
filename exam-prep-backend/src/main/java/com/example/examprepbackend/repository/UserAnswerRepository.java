package com.example.examprepbackend.repository;

import com.example.examprepbackend.entity.StudentAnswer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserAnswerRepository extends JpaRepository<StudentAnswer, Integer> {
    List<StudentAnswer> findByAttemptId(Integer attemptId);
}
