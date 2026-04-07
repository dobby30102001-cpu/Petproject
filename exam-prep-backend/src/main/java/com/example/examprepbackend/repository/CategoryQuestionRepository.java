package com.example.examprepbackend.repository;

import com.example.examprepbackend.entity.CategoryQuestion;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryQuestionRepository extends JpaRepository<CategoryQuestion, Integer> {
    CategoryQuestion findByName(String categoryName);
}
