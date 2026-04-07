package com.example.examprepbackend.service;

import com.example.examprepbackend.dto.request.admin.UpdateUserRequest;

import com.example.examprepbackend.dto.response.users.UserResponse;
import org.apache.coyote.BadRequestException;
import org.jspecify.annotations.Nullable;

public interface AdminUserService {
    UserResponse updateUser(Integer userId, UpdateUserRequest request) throws BadRequestException;

    void deleteUserByAdmin(Integer userId);

    Long countStudents();

     Long countTeachers();
}
