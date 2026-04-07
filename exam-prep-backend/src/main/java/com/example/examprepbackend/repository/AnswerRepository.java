package com.example.examprepbackend.repository;
import com.example.examprepbackend.entity.Answer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AnswerRepository extends JpaRepository<Answer, Integer> {
    List<Answer> findByQuestionIdIn(List<Integer> questionIds);

    @Modifying
    @Query("delete from Answer a where a.question.id = :questionId")
    void deleteByQuestion_Id(@Param("questionId") Integer questionId);

    List<Answer> findByQuestion_Id(Integer questionId);
}
