package com.example.examprepbackend.service;

import com.example.examprepbackend.dto.response.teacher.CategoryResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


public interface CategoriesService {
    Page<CategoryResponse> getAllCategories(Pageable pageable);
}
