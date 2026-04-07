package com.example.examprepbackend.controller;

import com.example.examprepbackend.common.BaseResponse;
import com.example.examprepbackend.dto.response.teacher.CategoryResponse;
import com.example.examprepbackend.service.CategoriesService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@Slf4j
@RequestMapping("/api/categories")
public class CategoriesController {
    private final CategoriesService categoriesService;

    @GetMapping
    public ResponseEntity<BaseResponse<Page<CategoryResponse>>> getAllCategories(Pageable pageable) {
        return ResponseEntity.ok(new BaseResponse<>(categoriesService.getAllCategories(pageable),"ALL Category"));
    }
}
