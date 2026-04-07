package com.example.examprepbackend.service.impl;

import com.example.examprepbackend.constant.ExamType;
import com.example.examprepbackend.dto.request.users.CreateUserRequest;
import com.example.examprepbackend.dto.response.exams.ExamAttemptResponse;
import com.example.examprepbackend.dto.response.users.*;
import com.example.examprepbackend.constant.Role;
import com.example.examprepbackend.dto.request.users.ChangePasswordRequest;
import com.example.examprepbackend.dto.request.users.UserProfileUpdateRequest;
import com.example.examprepbackend.dto.response.users.UserInfoResponse;
import com.example.examprepbackend.dto.response.users.UserSummaryResponse;
import com.example.examprepbackend.entity.Classes;
import com.example.examprepbackend.entity.ExamAttempt;
import com.example.examprepbackend.entity.Users;
import com.example.examprepbackend.exception.*;
import com.example.examprepbackend.mapper.UserMapper;
import com.example.examprepbackend.repository.*;
import com.example.examprepbackend.service.UsersService;
import com.example.examprepbackend.constant.Role;
import com.example.examprepbackend.constant.Status;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.dao.DataIntegrityViolationException;
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
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UsersServiceImpl implements UsersService {

    private final UsersRepository usersRepository;
    private final UserMapper userMapper;
    private final ClassRepository classRepository;
    private final ClassTeacherRepository classTeacherRepository;
    private final PasswordEncoder passwordEncoder;
    private final ModelMapper modelMapper;
    private final ExamAttemptRepository examAttemptRepository;

    private String normalizeEmail(String email) {
        if (email == null) return null;
        return email.trim().toLowerCase(Locale.ROOT);
    }

    private String normalizeUsername(String username) {
        if (username == null) return null;
        return username.trim();
    }

    private void checkDuplicate(String email, String username) {
        boolean emailExists = usersRepository.existsByEmail(email);
        boolean usernameExists = usersRepository.existsByUsernameIgnoreCase(username);

        if (emailExists && usernameExists) {
            throw new DuplicateResourceException("Email and username already exist");
        }
        if (emailExists) {
            throw new DuplicateResourceException("Email already exists");
        }
        if (usernameExists) {
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

        if (usersRepository.existsByUsernameIgnoreCase(username)) {
            throw new BadRequestException("Username đã tồn tại");
        }

        if (usersRepository.existsByEmailIgnoreCase(email)) {
            throw new BadRequestException("Email đã tồn tại");
        }

        Users user = userMapper.toEntity(request);
        user.setEmail(email);
        user.setUsername(username);
        applyFullName(user, request.getFullName());
        user.setPassword(passwordEncoder.encode("1234"));
        user.setRole(Role.valueOf(request.getRole().trim().toUpperCase()));
        user.setIsActive(true);
        user.setStatus(Status.ACTIVED);
        user.setCreatedDate(LocalDateTime.now());
        user.setFailCount(0);

        Users savedUser = usersRepository.save(user);
        return userMapper.toDto(savedUser);
    }

    @Override
    public List<UserResponse> getAllStudents() {
        return usersRepository.findByRole(Role.STUDENT).stream().map(this::convertToDto).toList();
    }

    @Override
    public List<UserResponse> getStudentsByClassId(Integer id) {
        Optional<Classes> classesOptional = classRepository.findById(id);
        if (classesOptional.isEmpty()) {
            throw new ApplicationException("Class not found");
        }

        return usersRepository.findByRoleAndClasses_Id(Role.STUDENT, id).stream().map(this::convertToDto).toList();
    }

    @Override
    public List<UserResponse> getAllTeachers() {
        return usersRepository.findByRole(Role.TEACHER).stream().map(this::convertToDto).toList();
    }

    @Override
    public List<UserResponse> getTeachersByClassId(Integer classId) {

        Optional<Classes> classesOptional = classRepository.findById(classId);
        if (classesOptional.isEmpty()) {
            throw new ApplicationException("Class not found");
        }

        List<Integer> teacherIdList = classTeacherRepository.findByClasses_Id(classId);

        return usersRepository.findByRoleAndIdIn(Role.TEACHER, teacherIdList).stream().map(this::convertToDto).toList();
    }

    @Transactional
    @Override
    public Boolean changePassword(Authentication authentication, ChangePasswordRequest changePasswordRequest) {

        //Check authentication
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new ApplicationException("Unauthorized");
        }

        //Check users
        String username = authentication.getName();
        Optional<Users> usersOptional = usersRepository.findByUsername(username);

        if (usersOptional.isEmpty()) {
            throw new ApplicationException("User not found");
        }

        //Check and change password
        String password = changePasswordRequest.getPassword();
        String newPassword = changePasswordRequest.getNewPassword();

        Users users = usersOptional.get();
        if (!passwordEncoder.matches(password, users.getPassword())) {
            throw new ApplicationException("Current password is incorrect");
        }

        users.setPassword(passwordEncoder.encode(newPassword));
        usersRepository.save(users);

        return true;
    }

    @Override
    public UserProfileResponse getCurrentUser(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException("Unauthorized");
        }
        String username = authentication.getName();

        Optional<Users> usersOptional = usersRepository.findByUsername(username);
        if (usersOptional.isEmpty()) {
            throw new ApplicationException("User not found");
        }
        Users users = usersOptional.get();

        UserProfileResponse userProfileResponse = modelMapper.map(users, UserProfileResponse.class);
        if ("STUDENT".equals(users.getRole().toString())) {
            userProfileResponse.setClassId(users.getClasses().getId());
            userProfileResponse.setClassName(users.getClasses().getName());
        }

        return userProfileResponse;
    }

    @Transactional
    @Override
    public UserSummaryResponse updateProfile(Authentication authentication, UserProfileUpdateRequest profileUpdateRequest) {

        //Check authentication
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new ApplicationException("Unauthorized");
        }

        //Check user
        String username = authentication.getName();
        Optional<Users> userOptional = usersRepository.findByUsername(username);

        if (userOptional.isEmpty()) {
            throw new ApplicationException("User not found");
        }

        //Check mail and update
        Users user = userOptional.get();

        String currentEmail = user.getEmail();
        String newEmail = profileUpdateRequest.getEmail();

        if (!newEmail.equals(currentEmail)) {
            Users existsByEmail = usersRepository.findUsersByEmail(newEmail);
            if (existsByEmail != null) {
                throw new ApplicationException("This email is already in use");
            }
        }

        modelMapper.map(profileUpdateRequest, user);
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