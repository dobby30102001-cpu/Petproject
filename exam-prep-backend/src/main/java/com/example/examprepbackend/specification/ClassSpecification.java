package com.example.examprepbackend.specification;


import com.example.examprepbackend.entity.Classes;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class ClassSpecification {

//    private String name;
//    private LocalDateTime createDate;

    public static Specification<Classes> hasNameLike(String name) {
        return (root, query, criteriaBuilder) -> {
            return criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), "%" + name + "%");
        };
    }

    public static Specification<Classes> hasAfterMinDate(LocalDate minDate) {
        return (root, query, criteriaBuilder) -> {
            return criteriaBuilder.greaterThan(root.get("createDate"), minDate);
        };
    }

    public static Specification<Classes> hasBeforeMinDate(LocalDate maxDate) {
        return (root, query, criteriaBuilder) -> {
            return criteriaBuilder.lessThan(root.get("createDate"), maxDate);
        };
    }

    public static Specification<Classes> hasCreateDate(LocalDate minDate, LocalDate maxDate) {
        return (root, query, criteriaBuilder) -> {
            return criteriaBuilder.between(root.get("createDate"), minDate, maxDate);
        };
    }

}
