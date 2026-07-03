package com.example.examprepbackend.service.impl;

import com.example.examprepbackend.constant.Role;
import com.example.examprepbackend.constant.Status;
import com.example.examprepbackend.dto.request.users.ChangePasswordRequest;
import com.example.examprepbackend.dto.request.users.CreateUserRequest;
import com.example.examprepbackend.dto.request.users.UserProfileUpdateRequest;
import com.example.examprepbackend.dto.response.users.UserProfileResponse;
import com.example.examprepbackend.dto.response.users.UserResponse;
import com.example.examprepbackend.dto.response.users.UserSummaryResponse;
import com.example.examprepbackend.entity.Users;
import com.example.examprepbackend.exception.ApplicationException;
import com.example.examprepbackend.exception.BadRequestException;
import com.example.examprepbackend.exception.DuplicateResourceException;
import com.example.examprepbackend.mapper.UserMapper;
import com.example.examprepbackend.repository.ClassRepository;
import com.example.examprepbackend.repository.ClassTeacherRepository;
import com.example.examprepbackend.repository.UsersRepository;
import com.example.examprepbackend.service.UsersService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class UsersServiceImpl implements UsersService {

    private final UsersRepository usersRepository;
    private final UserMapper userMapper;
    private final ClassRepository classRepository;
    private final ClassTeacherRepository classTeacherRepository;
    private final PasswordEncoder passwordEncoder;
    private final ModelMapper modelMapper;

    private String normalizeEmail(String email) {
        if (email == null) return null;
        return email.trim().toLowerCase(Locale.ROOT);
    }

    private String normalizeUsername(String username) {
        if (username == null) return null;
        return username.trim();
    }

    private void checkDuplicate(String email, String username) {
        if (usersRepository.existsByEmailIgnoreCase(email)) {
            throw new DuplicateResourceException("Email already exists");
        }
        if (usersRepository.existsByUsernameIgnoreCase(username)) {
            throw new DuplicateResourceException("Username already exists");
        }
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

        // Họ (lastName) = từ đầu tiên
        user.setLastName(parts[0]);

        // Tên (firstName) = phần còn lại
        String firstName = String.join(" ", java.util.Arrays.copyOfRange(parts, 1, parts.length));
        user.setFirstName(firstName);
    }

    private UserResponse convertToDto(Users users) {
        UserResponse userResponse = new UserResponse();

        BeanUtils.copyProperties(users, userResponse);

        return userResponse;

    }

    @Transactional
    @Override
    public Page<UserResponse> getAllUsers(Pageable pageable) {
        return usersRepository.findAll(pageable).map(this::convertToDto);
    }

    @Override
    public UserSummaryResponse createUser(CreateUserRequest request) {
        String email = normalizeEmail(request.getEmail());
        String username = normalizeUsername(request.getUsername());

        checkDuplicate(email, username);
        Role role = parseRole(request.getRole());

        Users user = userMapper.toEntity(request);
        user.setEmail(email);
        user.setUsername(username);
        applyFullName(user, request.getFullName());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(role);
        user.setIsActive(true);
        user.setStatus(Status.ACTIVED);
        user.setCreatedDate(LocalDateTime.now());
        user.setFailCount(0);

        return userMapper.toDto(usersRepository.save(user));
    }

    private Role parseRole(String role) {
        try {
            return Role.valueOf(role.trim().toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Invalid role: " + role);
        }
    }

    @Override
    public List<UserResponse> getAllStudents() {
        return usersRepository.findByRole(Role.STUDENT).stream().map(this::convertToDto).toList();
    }

    @Override
    public List<UserResponse> getStudentsByClassId(Integer id) {
        requireClassExists(id);
        return usersRepository.findByRoleAndClasses_Id(Role.STUDENT, id).stream().map(this::convertToDto).toList();
    }

    @Override
    public List<UserResponse> getAllTeachers() {
        return usersRepository.findByRole(Role.TEACHER).stream().map(this::convertToDto).toList();
    }

    @Override
    public List<UserResponse> getTeachersByClassId(Integer classId) {
        requireClassExists(classId);
        List<Integer> teacherIdList = classTeacherRepository.findByClasses_Id(classId);
        return usersRepository.findByRoleAndIdIn(Role.TEACHER, teacherIdList).stream().map(this::convertToDto).toList();
    }

    private void requireClassExists(Integer id) {
        if (!classRepository.existsById(id)) {
            throw new ApplicationException("Class not found");
        }
    }

    @Transactional
    @Override
    public Boolean changePassword(Authentication authentication, ChangePasswordRequest changePasswordRequest) {
        Users user = requireAuthenticatedUser(authentication);

        if (!passwordEncoder.matches(changePasswordRequest.getPassword(), user.getPassword())) {
            throw new ApplicationException("Current password is incorrect");
        }

        user.setPassword(passwordEncoder.encode(changePasswordRequest.getNewPassword()));
        usersRepository.save(user);
        return true;
    }

    @Override
    public UserProfileResponse getCurrentUser(Authentication authentication) {
        Users user = requireAuthenticatedUser(authentication);

        UserProfileResponse response = modelMapper.map(user, UserProfileResponse.class);
        if (user.getRole() == Role.STUDENT && user.getClasses() != null) {
            response.setClassId(user.getClasses().getId());
            response.setClassName(user.getClasses().getName());
        }
        return response;
    }

    private Users requireAuthenticatedUser(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new ApplicationException("Unauthorized");
        }
        return usersRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new ApplicationException("User not found"));
    }

    @Transactional
    @Override
    public UserSummaryResponse updateProfile(Authentication authentication, UserProfileUpdateRequest profileUpdateRequest) {
        Users user = requireAuthenticatedUser(authentication);

        String newEmail = normalizeEmail(profileUpdateRequest.getEmail());
        if (newEmail != null && !newEmail.equalsIgnoreCase(user.getEmail())
                && usersRepository.existsByEmailIgnoreCase(newEmail)) {
            throw new DuplicateResourceException("This email is already in use");
        }

        modelMapper.map(profileUpdateRequest, user);
        if (newEmail != null) {
            user.setEmail(newEmail);
        }
        usersRepository.save(user);
        return modelMapper.map(user, UserSummaryResponse.class);
    }
    @Override
    public long countTeachers() {
        return usersRepository.countByRole(Role.TEACHER);
    }

    @Override
    public long countStudents() {
        return usersRepository.countByRole(Role.STUDENT);
    }


}