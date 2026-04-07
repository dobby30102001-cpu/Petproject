package com.example.examprepbackend.specification;

import com.example.examprepbackend.constant.ExamType;
import com.example.examprepbackend.entity.Classes;
import com.example.examprepbackend.entity.Exam;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.util.List;

public class ExamSpecification {
//    private String code;
//    private String title;
//    private CategoryQuestion category;
//    private LocalDateTime createDate;

    public static Specification<Exam> hasCreatorUsername(String username) {
        return (root, query, criteriaBuilder) -> {
            return criteriaBuilder.equal(root.get("creator").get("username"), username);
        };
    }

    public static Specification<Exam> hasCodeLike(String code) {
        return (root, query, criteriaBuilder) -> {
            return criteriaBuilder.like(criteriaBuilder.lower(root.get("code")), "%" + code.toLowerCase() + "%");
        };
    }

    public static Specification<Exam> hasTitleLike(String title) {
        return (root, query, criteriaBuilder) -> {
            return criteriaBuilder.like(criteriaBuilder.lower(root.get("title")), "%" + title.toLowerCase() + "%");
        };
    }

    public static Specification<Exam> hasCategoryName(String categoryName) {
        return (root, query, criteriaBuilder) -> {
            return criteriaBuilder.equal(root.get("category").get("name"),categoryName);
        };
    }

    public static Specification<Exam> hasIdIn(List<Integer> ids) {
        return (root, query, criteriaBuilder) -> {
            return root.get("id").in(ids);
        };
    }

    public static Specification<Exam> hasExamType(ExamType examType) {
        return (root, query, criteriaBuilder) -> {
            return criteriaBuilder.equal(root.get("examType"), examType);
        };
    }


    public static Specification<Exam> hasCategoryId(Integer categoryId) {
        return (root , query , criteriaBuilder) -> {
            return criteriaBuilder.equal(root.get("category").get("id"), categoryId);
        };
    }

    public static Specification<Exam> hasAfterMinDate(LocalDate minDate) {
        return (root, query, criteriaBuilder) -> {
            return criteriaBuilder.greaterThan(root.get("createDate"), minDate);
        };
    }

    public static Specification<Exam> hasBeforeMaxDate(LocalDate maxDate) {
        return (root, query, criteriaBuilder) -> {
            return criteriaBuilder.lessThan(root.get("createDate"), maxDate);
        };
    }

    public static Specification<Exam> hasCreateDate(LocalDate minDate, LocalDate maxDate) {
        return (root, query, criteriaBuilder) -> {
            return criteriaBuilder.between(root.get("createDate"), minDate, maxDate);
        };
    }


    public static Specification<Exam> hasClassId(Integer classId) {
        return (root, query, criteriaBuilder) -> {
            if (classId == null) return null;
            return criteriaBuilder.equal(root.join("examClasses").join("clazz").get("id"), classId);
        };

    }
}
