package com.example.examprepbackend.controller.Admin;


import com.example.examprepbackend.common.BaseResponse;
import com.example.examprepbackend.dto.request.clazz.ClassRequest;
import com.example.examprepbackend.dto.request.clazz.ClassRequestParam;
import com.example.examprepbackend.dto.response.clazz.ClassDetailResponse;
import com.example.examprepbackend.dto.response.clazz.ClassResponse;
import com.example.examprepbackend.service.ClassService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/admin/classes")
@RequiredArgsConstructor
public class AdminClassController {

    private final ClassService classService;

    @GetMapping
    public ResponseEntity<BaseResponse<Page<ClassResponse>>> getAllClasses(ClassRequestParam classRequestParam,
                                                                           @PageableDefault(size = 5, sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok().body(new BaseResponse<>(classService.getAllClasses(classRequestParam, pageable), "Get All Classes"));
    }

    @GetMapping("/teacher")
    public ResponseEntity<BaseResponse<Page<ClassDetailResponse>>> getClassesByTeacher(Authentication authentication,
                                                                                       @PageableDefault(size = 5, sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok().body(new BaseResponse<>(classService.getClassesByTeacher(authentication, pageable), "Get Classes by teacher"));
    }

    @PostMapping
    public ResponseEntity<BaseResponse<ClassResponse>> createClass(@RequestBody @Valid ClassRequest classRequest) {
        return ResponseEntity.ok().body(new BaseResponse<>(classService.createClass(classRequest), "Created class"));
    }

    @PutMapping("/{id}")
    public ResponseEntity<BaseResponse<ClassResponse>> updateClass(@PathVariable Integer id, @RequestBody @Valid ClassRequest classRequest) {
        return ResponseEntity.ok().body(new BaseResponse<>(classService.updateClass(id, classRequest), "Updated class"));
    }

    @PutMapping("/{id}/students")
    public ResponseEntity<BaseResponse<Boolean>> addStudentsToClass(@PathVariable Integer id, @RequestBody List<Integer> studentIdList) {
        return ResponseEntity.ok().body(new BaseResponse<>(classService.addStudentsToClass(id, studentIdList), "Add students to class"));
    }

    @PutMapping("/{id}/teachers")
    public ResponseEntity<BaseResponse<Boolean>> addTeachersToClass(@PathVariable Integer id, @RequestBody List<Integer> teacherIdList) {
        return ResponseEntity.ok().body(new BaseResponse<>(classService.addTeachersToClass(id, teacherIdList), "Add teachers to class"));
    }

    @PutMapping("/{id}/exams")
    public ResponseEntity<BaseResponse<Boolean>> addExamsToClass(@PathVariable Integer id, @RequestBody List<Integer> examIds) {
        return ResponseEntity.ok().body(new BaseResponse<>(classService.addExamsToClass(id, examIds), "Add exams to class"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<BaseResponse<Boolean>> deleteById(@PathVariable Integer id) {
        return ResponseEntity.ok().body(new BaseResponse<>(classService.deleteById(id), "Class deleted"));
    }
    @GetMapping("/count")
    public Long countClasses() {

        return classService.countClass();
    }

}
