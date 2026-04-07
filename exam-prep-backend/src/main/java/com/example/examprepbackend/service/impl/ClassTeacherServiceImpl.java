package com.example.examprepbackend.service.impl;

import com.example.examprepbackend.entity.ClassTeacher;
import com.example.examprepbackend.entity.ClassTeacherId;
import com.example.examprepbackend.entity.Classes;
import com.example.examprepbackend.entity.Users;
import com.example.examprepbackend.repository.ClassTeacherRepository;
import com.example.examprepbackend.service.ClassTeacherService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ClassTeacherServiceImpl implements ClassTeacherService {
    private final ClassTeacherRepository classTeacherRepository;

    @Override
    public void createClassTeachers(Classes classes, List<Users> teacherList) {

        for (Users teacher : teacherList) {
            ClassTeacher classTeacher = new ClassTeacher();

            ClassTeacherId classTeacherId = new ClassTeacherId();
            classTeacherId.setClassId(classes.getId());
            classTeacherId.setTeacherId(teacher.getId());

            classTeacher.setId(classTeacherId);
            classTeacher.setClasses(classes);
            classTeacher.setTeacher(teacher);

            classTeacherRepository.save(classTeacher);
        }


    }
}
