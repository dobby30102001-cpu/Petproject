package com.example.examprepbackend.repository;

import com.example.examprepbackend.entity.ClassExam;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ClassExamRepository extends JpaRepository<ClassExam, Integer> {

    // Lấy danh sách examId theo classId
    @Query("select ce.examId from ClassExam ce where ce.classId = :classId ")
    List<Integer> findByClassId(@Param("classId") Integer classId);

    @Modifying
    @Query("delete from ClassExam cl where cl.classId = :classId")
    void deleteByClasses_Id(@Param("classId") Integer classId);

    ClassExam findByClassIdAndExamId(Integer classId, Integer examId);
}
