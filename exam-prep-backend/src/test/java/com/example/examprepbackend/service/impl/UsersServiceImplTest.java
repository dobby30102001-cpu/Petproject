package com.example.examprepbackend.service.impl;

import com.example.examprepbackend.constant.Role;
import com.example.examprepbackend.dto.request.users.CreateUserRequest;
import com.example.examprepbackend.dto.response.users.UserSummaryResponse;
import com.example.examprepbackend.entity.Users;
import com.example.examprepbackend.exception.BadRequestException;
import com.example.examprepbackend.exception.DuplicateResourceException;
import com.example.examprepbackend.mapper.UserMapper;
import com.example.examprepbackend.repository.UsersRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UsersServiceImplTest {

    @Mock
    private UsersRepository usersRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UsersServiceImpl usersService;

    private CreateUserRequest buildValidRequest() {
        CreateUserRequest req = new CreateUserRequest();
        req.setEmail("test@example.com");
        req.setUsername("testuser");
        req.setPassword("password");
        req.setFullName("Test User");
        req.setRole("STUDENT");
        return req;
    }

    @Test
    void createUser_success() {
        CreateUserRequest req = buildValidRequest();

        when(userMapper.toEntity(any(CreateUserRequest.class))).thenReturn(new Users());
        when(usersRepository.existsByEmailIgnoreCase("test@example.com")).thenReturn(false);
        when(usersRepository.existsByUsernameIgnoreCase("testuser")).thenReturn(false);
        when(passwordEncoder.encode("password")).thenReturn("encoded-password");

        Users saved = new Users();
        saved.setId(1);
        saved.setEmail("test@example.com");
        saved.setUsername("testuser");
        saved.setRole(Role.STUDENT);
        when(usersRepository.save(any(Users.class))).thenReturn(saved);

        UserSummaryResponse dto = new UserSummaryResponse();
        dto.setEmail("test@example.com");
        dto.setUsername("testuser");
        when(userMapper.toDto(eq(saved))).thenReturn(dto);

        UserSummaryResponse result = usersService.createUser(req);

        assertNotNull(result);
        assertEquals("test@example.com", result.getEmail());
        assertEquals("testuser", result.getUsername());
    }

    @Test
    void createUser_duplicateEmail_throws() {
        CreateUserRequest req = buildValidRequest();
        req.setEmail("dup@example.com");

        when(usersRepository.existsByEmailIgnoreCase("dup@example.com")).thenReturn(true);

        DuplicateResourceException ex = assertThrows(
                DuplicateResourceException.class, () -> usersService.createUser(req));
        assertTrue(ex.getMessage().contains("Email"));
    }

    @Test
    void createUser_duplicateUsername_throws() {
        CreateUserRequest req = buildValidRequest();
        req.setEmail("dup2@example.com");
        req.setUsername("dupuser2");

        when(usersRepository.existsByEmailIgnoreCase("dup2@example.com")).thenReturn(false);
        when(usersRepository.existsByUsernameIgnoreCase("dupuser2")).thenReturn(true);

        DuplicateResourceException ex = assertThrows(
                DuplicateResourceException.class, () -> usersService.createUser(req));
        assertTrue(ex.getMessage().contains("Username"));
    }

    @Test
    void createUser_invalidRole_throws() {
        CreateUserRequest req = buildValidRequest();
        req.setRole("NOT_A_ROLE");

        when(usersRepository.existsByEmailIgnoreCase("test@example.com")).thenReturn(false);
        when(usersRepository.existsByUsernameIgnoreCase("testuser")).thenReturn(false);

        BadRequestException ex = assertThrows(
                BadRequestException.class, () -> usersService.createUser(req));
        assertTrue(ex.getMessage().contains("Invalid role"));
    }

    @Test
    void createUser_normalizesEmailToLowercase() {
        CreateUserRequest req = buildValidRequest();
        req.setEmail("  MixedCase@Example.COM  ");

        when(usersRepository.existsByEmailIgnoreCase("mixedcase@example.com")).thenReturn(false);
        when(usersRepository.existsByUsernameIgnoreCase("testuser")).thenReturn(false);
        when(userMapper.toEntity(any(CreateUserRequest.class))).thenReturn(new Users());
        when(passwordEncoder.encode("password")).thenReturn("encoded-password");
        when(usersRepository.save(any(Users.class))).thenReturn(new Users());
        when(userMapper.toDto(any(Users.class))).thenReturn(new UserSummaryResponse());

        usersService.createUser(req);
    }
}
