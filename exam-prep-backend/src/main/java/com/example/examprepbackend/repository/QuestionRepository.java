package com.example.examprepbackend.repository;

import com.example.examprepbackend.constant.DifficultyLevel;
import com.example.examprepbackend.constant.Role;
import com.example.examprepbackend.entity.Question;
import com.example.examprepbackend.entity.Users;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface QuestionRepository extends JpaRepository<Question, Integer>, JpaSpecificationExecutor<Question> {

    List<Question> findByIdIn(List<Integer> ids);

    //stats
    long countByCreator_Username(String username);

    long countByCreator_UsernameAndDifficultyLevel(String username, DifficultyLevel level);

    // =========================
    // Student / Exam
    // Question không còn examId trực tiếp,
    // nên phải join qua bảng trung gian ExamQuestion
    // =========================
    @Query("""
select q
from Question q
join ExamQuestion eq on eq.question.id = q.id
where eq.exam.id = :examId
""")
    List<Question> findQuestionsByExamId(@Param("examId") Integer examId);

    @Query("""
        select q
        from Question q
        join ExamQuestion eq on eq.question.id = q.id
        where eq.exam.id in :examIds
          and q.creator.role = :role
    """)
    Page<Question> findQuestionsByExamIdsAndCreatorRole(@Param("examIds") List<Integer> examIds,
                                                        @Param("role") Role role,
                                                        Pageable pageable);
}



