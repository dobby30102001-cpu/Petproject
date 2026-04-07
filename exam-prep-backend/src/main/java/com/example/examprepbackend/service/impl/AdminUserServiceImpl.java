package com.example.examprepbackend.service.impl;

import com.example.examprepbackend.config.SecurityUtils;
import com.example.examprepbackend.constant.Role;
import com.example.examprepbackend.constant.Status;
import com.example.examprepbackend.dto.request.admin.UpdateUserRequest;
import com.example.examprepbackend.dto.response.users.UserResponse;
import com.example.examprepbackend.entity.Users;

import com.example.examprepbackend.exception.ApplicationException;
import com.example.examprepbackend.exception.ResourceNotFoundException;
import com.example.examprepbackend.repository.ClassTeacherRepository;
import com.example.examprepbackend.repository.UsersRepository;
import com.example.examprepbackend.service.AdminUserService;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;



@Service
@RequiredArgsConstructor
@Transactional
public class AdminUserServiceImpl implements AdminUserService {

    private final UsersRepository usersRepository;
    private final ClassTeacherRepository classTeacherRepository;

    @Override
    public UserResponse updateUser(Integer userId, UpdateUserRequest request) throws BadRequestException {
        Users user = usersRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        String normalizedUsername = normalizeUsername(request.getUsername());
        String normalizedEmail = normalizeEmail(request.getEmail());

        validateUniqueFields(userId, normalizedUsername, normalizedEmail);

        user.setUsername(normalizedUsername);
        user.setEmail(normalizedEmail);
        applyFullName(user, request.getFullName());
        user.setRole(request.getRole());
        user.setIsActive(request.getActive());

        return mapToResponse(usersRepository.save(user));
    }

    private void validateUniqueFields(Integer userId, String username, String email) throws BadRequestException {
        if (usersRepository.existsByUsernameAndIdNot(username, userId)) {
            throw new ApplicationException("Username đã tồn tại");
        }

        if (usersRepository.existsByEmailAndIdNot(email, userId)) {
            throw new ApplicationException("Email đã tồn tại");
        }
    }

    private String normalizeUsername(String username) {
        return username == null ? null : username.trim();
    }

    private String normalizeEmail(String email) {
        return email == null ? null : email.trim().toLowerCase();
    }

    private UserResponse mapToResponse(Users user) {
        return UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .fullName(buildFullName(user))
                .role(user.getRole())
                .isActive(user.getIsActive())
                .build();
    }

    private void applyFullName(Users user, String fullName) {
        if (fullName == null || fullName.trim().isEmpty()) {
            user.setFirstName(null);
            user.setLastName(null);
            return;
        }

        String[] parts = fullName.trim().split("\\s+");

        if (parts.length == 1) {
            user.setFirstName(parts[0]);
            user.setLastName("");
            return;
        }

        user.setLastName(parts[0]);
        String firstName = String.join(" ", java.util.Arrays.copyOfRange(parts, 1, parts.length));
        user.setFirstName(firstName);
    }

    private String buildFullName(Users user) {
        String firstName = user.getFirstName();
        String lastName = user.getLastName();

        if (firstName == null && lastName == null) {
            return null;
        }

        if (lastName == null || lastName.isBlank()) {
            return firstName;
        }

        if (firstName == null || firstName.isBlank()) {
            return lastName;
        }

        return lastName + " " + firstName;
    }
    @Override
    @Transactional
    public void deleteUserByAdmin(Integer userId) {
        Users targetUser = usersRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (Boolean.FALSE.equals(targetUser.getIsActive())) {
            throw new RuntimeException("User already deleted");
        }

        boolean isAssignedToClass = classTeacherRepository.existsByTeacherId(userId);
        if (isAssignedToClass) {
            throw new RuntimeException("Cannot delete user because this teacher is assigned to class");
        }

        targetUser.setIsActive(false);
        usersRepository.save(targetUser);
    }

    @Override
    public Long countStudents() {
        return usersRepository.countByRole(Role.STUDENT);
    }

    @Override
    public Long countTeachers() {
        return usersRepository.countByRole(Role.TEACHER);
    }

}
