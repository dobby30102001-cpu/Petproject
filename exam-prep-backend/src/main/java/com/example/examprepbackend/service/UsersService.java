package com.example.examprepbackend.service;

import java.util.List;

import com.example.examprepbackend.constant.ExamType;
import com.example.examprepbackend.constant.Role;
import com.example.examprepbackend.dto.request.users.ChangePasswordRequest;
import com.example.examprepbackend.dto.request.users.UserProfileUpdateRequest;

import com.example.examprepbackend.dto.response.exams.ExamAttemptResponse;
import com.example.examprepbackend.dto.response.users.UserProfileResponse;
import com.example.examprepbackend.dto.response.users.UserResponse;
import com.example.examprepbackend.dto.response.users.UserInfoResponse;
import com.example.examprepbackend.dto.response.users.UserSummaryResponse;
import com.example.examprepbackend.entity.ExamAttempt;
import com.example.examprepbackend.entity.Users;
import com.example.examprepbackend.dto.request.users.CreateUserRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;


public interface UsersService {

    Page<UserResponse> getAllUsers(Pageable pageable);

    UserSummaryResponse createUser(CreateUserRequest request);

    List<UserResponse> getAllStudents();

    List<UserResponse> getStudentsByClassId(Integer id);

    List<UserResponse> getAllTeachers();

    List<UserResponse> getTeachersByClassId(Integer classId);

    Boolean changePassword(Authentication authentication, ChangePasswordRequest changePasswordRequest);

    UserProfileResponse getCurrentUser(Authentication authentication);

    UserSummaryResponse updateProfile(Authentication authentication, UserProfileUpdateRequest profileUpdateRequest);
    long countTeachers();
    long countStudents();

}