package com.example.examprepbackend.service.impl;

import com.example.examprepbackend.dto.response.teacher.CategoryResponse;
import com.example.examprepbackend.entity.CategoryQuestion;
import com.example.examprepbackend.repository.CategoryQuestionRepository;
import com.example.examprepbackend.service.CategoriesService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class CategoriesServiceImpl implements CategoriesService {

    private  final CategoryQuestionRepository categoryQuestionRepository;


    @Override
    public Page<CategoryResponse> getAllCategories(Pageable pageable) {

        Page<CategoryQuestion> categories = categoryQuestionRepository.findAll(pageable);

        return categories.map(c -> {
            CategoryResponse dto = new CategoryResponse();
            dto.setId(c.getId());
            dto.setName(c.getName());
            return dto;
        });
    }
}
