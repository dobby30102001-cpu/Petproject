package com.example.examprepbackend.controller.users;

import com.example.examprepbackend.common.BaseResponse;
import com.example.examprepbackend.dto.request.users.ChangePasswordRequest;
import com.example.examprepbackend.dto.request.users.CreateUserRequest;
import com.example.examprepbackend.dto.request.users.UserProfileUpdateRequest;
import com.example.examprepbackend.dto.response.questions.QuestionResponse;
import com.example.examprepbackend.dto.response.users.UserProfileResponse;
import com.example.examprepbackend.dto.response.users.UserResponse;
import com.example.examprepbackend.dto.response.users.UserSummaryResponse;
import com.example.examprepbackend.service.ExamAttemptService;
import com.example.examprepbackend.service.QuestionService;
import com.example.examprepbackend.service.UsersService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UsersController {
    private final UsersService usersService;
    private final QuestionService questionService;
    private final ExamAttemptService examAttemptService;

    @GetMapping
    public ResponseEntity<BaseResponse<Page<UserResponse>>> getAllUsers(@PageableDefault(size = 5, sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok().body(new BaseResponse<>(usersService.getAllUsers(pageable), "Get all users"));
    }

    @GetMapping("/students")
    public ResponseEntity<BaseResponse<List<UserResponse>>> getAllStudents() {
        return ResponseEntity.ok().body(new BaseResponse<>(usersService.getAllStudents(), "Get All Students"));
    }

    @GetMapping("/students/class-id/{id}")
    public ResponseEntity<BaseResponse<List<UserResponse>>> getStudentsByClassId(@PathVariable Integer id) {
        return ResponseEntity.ok().body(new BaseResponse<>(usersService.getStudentsByClassId(id), "Get Students by class id"));
    }

    @GetMapping("/teachers")
    public ResponseEntity<BaseResponse<List<UserResponse>>> getAllTeachers() {
        return ResponseEntity.ok().body(new BaseResponse<>(usersService.getAllTeachers(), "Get all teachers"));
    }

    @GetMapping("/teachers/class-id/{id}")
    public ResponseEntity<BaseResponse<List<UserResponse>>> getTeachersByClassId(@PathVariable Integer id) {
        return ResponseEntity.ok().body(new BaseResponse<>(usersService.getTeachersByClassId(id), "Get teachers by class id"));
    }

    @PutMapping("/change-password")
    public ResponseEntity<BaseResponse<Boolean>> changePassword(Authentication authentication,
                                                                @Valid @RequestBody ChangePasswordRequest changePasswordRequest) {
        return ResponseEntity.ok().body(new BaseResponse<>(usersService.changePassword(authentication, changePasswordRequest), "Password changed successfully"));
    }

    @PutMapping("/profile")
    public ResponseEntity<BaseResponse<UserSummaryResponse>> updateProfile(Authentication authentication, @Valid @RequestBody UserProfileUpdateRequest profileUpdateRequest) {
        return ResponseEntity.ok().body(new BaseResponse<>(usersService.updateProfile(authentication, profileUpdateRequest), "Profile updated successfully"));
    }

    @GetMapping("/me")
    public ResponseEntity<BaseResponse<UserProfileResponse>> getUser(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new BaseResponse<>(null, "Please login to access this resource"));
        }
        UserProfileResponse userSummary = usersService.getCurrentUser(authentication);
        return ResponseEntity.ok().body(new BaseResponse<>(userSummary, "Get current user successfully"));
    }


    @PostMapping
    public ResponseEntity<BaseResponse<UserSummaryResponse>> createUser(@RequestBody @Valid CreateUserRequest request) {
        UserSummaryResponse dto = usersService.createUser(request);
        return ResponseEntity.ok(new BaseResponse<>(dto, "User created successfully"));
    }

    @GetMapping("/questions")
    public ResponseEntity<BaseResponse<Page<QuestionResponse>>> getAllQuestionsByStudent(Pageable pageable) {
        return ResponseEntity.ok().body(new BaseResponse<>(questionService.getAllQuestionsByStudent(pageable), "Get All Question Succcesfull!"));
    }

}
