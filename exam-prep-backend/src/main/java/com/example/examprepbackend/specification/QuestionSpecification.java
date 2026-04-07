package com.example.examprepbackend.specification;

import com.example.examprepbackend.constant.DifficultyLevel;
import com.example.examprepbackend.entity.Exam;
import com.example.examprepbackend.entity.Question;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class QuestionSpecification {

    public static Specification<Question> hasContentLike(String content) {
        return (root, query, cb) -> {
            if (content == null || content.isEmpty()) return null;

            return cb.like(
                    cb.upper(root.get("content")),
                    "%" + content.toUpperCase() + "%"
            );
        };
    }

    public static Specification<Question> hasDifficulty(DifficultyLevel difficulty) {
        return (root, query, cb) -> {
            if (difficulty == null) return null;

            return cb.equal(root.get("difficultyLevel"), difficulty);
        };
    }

    public static Specification<Question> hasCategoryId(Integer categoryId) {
        return (root, query, cb) -> {
            if (categoryId == null) return null;

            return cb.equal(root.get("category").get("id"), categoryId);
        };
    }

    public static Specification<Question> hasCreatorId(Integer creatorId) {
        return (root, query, cb) -> {
            if (creatorId == null) return null;

            return cb.equal(root.get("creator").get("id"), creatorId);
        };
    }

    public static Specification<Question> hasCreateDate(LocalDate minDate, LocalDate maxDate) {
        return (root, query, criteriaBuilder) -> {
            return criteriaBuilder.between(root.get("createDate"), minDate, maxDate);
        };
    }
    //lọc theo name
    public static Specification<Question> hasCreatorUsername(String username) {
        return (root, query, cb) ->
                cb.equal(root.get("creator").get("username"), username);
    }
}
