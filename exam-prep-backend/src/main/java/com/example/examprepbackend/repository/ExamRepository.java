package com.example.examprepbackend.repository;

import com.example.examprepbackend.constant.ExamType;
import com.example.examprepbackend.entity.Exam;
import org.springframework.data.domain.Limit;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Collection;
import java.util.List;

public interface ExamRepository extends JpaRepository<Exam, Integer>, JpaSpecificationExecutor<Exam> {

    List<Exam> findExamsByCreator_Username(String creatorUsername);

    Exam findByCode(String code);

    List<Exam> findByIdIn(List<Integer> idList);

    Page<Exam> findPageByExamTypeAndIdIn(ExamType examType, List<Integer> idList, Pageable pageable);

    //stats
    long countByCreator_Username(String username);
}
