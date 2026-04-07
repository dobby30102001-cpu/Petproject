package com.example.examprepbackend.service;

import com.example.examprepbackend.dto.request.clazz.ClassRequest;
import com.example.examprepbackend.dto.request.clazz.ClassRequestParam;
import com.example.examprepbackend.dto.response.clazz.ClassDetailResponse;
import com.example.examprepbackend.dto.response.clazz.ClassResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;

import java.util.List;

public interface ClassService {

    Page<ClassResponse> getAllClasses(ClassRequestParam classRequestParam, Pageable pageable);

    Page<ClassDetailResponse> getClassesByTeacher(Authentication authentication, Pageable pageable);

    ClassResponse createClass(ClassRequest classRequest);

    ClassResponse updateClass(Integer id, ClassRequest classRequest);

    Boolean addStudentsToClass(Integer id, List<Integer> studentIdList);

    Boolean addTeachersToClass(Integer id, List<Integer> teacherIdList);

    Boolean addExamsToClass(Integer id, List<Integer> examIds);

    Boolean deleteById(Integer id);
    long countClass ();

}
