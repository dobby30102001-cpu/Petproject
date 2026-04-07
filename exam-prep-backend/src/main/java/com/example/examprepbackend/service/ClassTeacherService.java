package com.example.examprepbackend.service;

import com.example.examprepbackend.entity.Classes;
import com.example.examprepbackend.entity.Users;

import java.util.List;

public interface ClassTeacherService {

    void createClassTeachers(Classes classes, List<Users> teacherList);

}
