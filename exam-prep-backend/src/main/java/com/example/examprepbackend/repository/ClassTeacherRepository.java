package com.example.examprepbackend.repository;

import com.example.examprepbackend.entity.ClassTeacher;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ClassTeacherRepository extends JpaRepository<ClassTeacher, Integer> {

    boolean existsByTeacherId(Integer teacherId);
    @Query("select cl.teacher.id from ClassTeacher cl where cl.classes.id = :classId")
    List<Integer> findByClasses_Id(@Param("classId") Integer classId);

    @Query("select cl.classes.id from ClassTeacher cl where cl.teacher.username = :username")
    List<Integer> findByTeacher_Username(@Param(("username")) String username);

    @Modifying
    @Query("delete from ClassTeacher cl where cl.classes.id = :classId")
    void deleteByClasses_Id(@Param("classId") Integer classId);

    Long countByClasses_Id(Integer classId);

}
