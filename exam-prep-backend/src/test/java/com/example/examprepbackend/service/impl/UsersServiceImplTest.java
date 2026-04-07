package com.example.examprepbackend.service.impl;

import com.example.examprepbackend.dto.request.users.CreateUserRequest;
import com.example.examprepbackend.dto.response.users.UserSummaryResponse;
import com.example.examprepbackend.entity.Users;
import com.example.examprepbackend.mapper.UserMapper;
import com.example.examprepbackend.repository.UsersRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
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

    @Test
    void createUser_success() {
        CreateUserRequest req = new CreateUserRequest();
        req.setEmail("test@example.com");
        req.setUsername("testuser");
        req.setPassword("password");
        req.setFirstName("Test");
        req.setLastName("User");

        Users mappedUser = new Users();
        // userMapper.toEntity(request) -> mappedUser
        when(userMapper.toEntity(any(CreateUserRequest.class))).thenReturn(mappedUser);

        when(usersRepository.findByEmailOrUsername("test@example.com", "testuser")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("password")).thenReturn("encoded-password");

        Users saved = new Users();
        saved.setId(1);
        saved.setEmail("test@example.com");
        saved.setUsername("testuser");
        saved.setFirstName("Test");
        saved.setLastName("User");
        saved.setCreatedDate(LocalDateTime.now());

        when(usersRepository.save(any(Users.class))).thenReturn(saved);

        UserSummaryResponse dto = new UserSummaryResponse();
        dto.setEmail("test@example.com");
        dto.setUsername("testuser");
        dto.setFirstName("Test");
        dto.setLastName("User");

        when(userMapper.toDto(eq(saved))).thenReturn(dto);

        UserSummaryResponse result = usersService.createUser(req);

        assertNotNull(result);
        assertEquals("test@example.com", result.getEmail());
        assertEquals("testuser", result.getUsername());
        assertEquals("Test", result.getFirstName());
        assertEquals("User", result.getLastName());
    }

    @Test
    void createUser_duplicateEmail_throws() {
        CreateUserRequest req = new CreateUserRequest();
        req.setEmail("dup@example.com");
        req.setUsername("dupuser");
        req.setPassword("password");
        req.setFirstName("Dup");
        req.setLastName("User");

        Users existing = new Users();
        existing.setEmail("dup@example.com");

        when(usersRepository.findByEmailOrUsername("dup@example.com", "dupuser")).thenReturn(Optional.of(existing));

        RuntimeException ex = assertThrows(RuntimeException.class, () -> usersService.createUser(req));
        assertTrue(ex.getMessage().contains("Email already exists") || ex.getMessage().contains("Email or username already exists") );
    }

    @Test
    void createUser_duplicateUsername_throws() {
        CreateUserRequest req = new CreateUserRequest();
        req.setEmail("dup2@example.com");
        req.setUsername("dupuser2");
        req.setPassword("password");
        req.setFirstName("Dup");
        req.setLastName("User");

        Users existing = new Users();
        existing.setUsername("dupuser2");

        when(usersRepository.findByEmailOrUsername("dup2@example.com", "dupuser2")).thenReturn(Optional.of(existing));

        RuntimeException ex = assertThrows(RuntimeException.class, () -> usersService.createUser(req));
        assertTrue(ex.getMessage().contains("Username already exists") || ex.getMessage().contains("Email or username already exists") );
    }

    @Test
    void createUser_blankEmail_throws() {
        CreateUserRequest req = new CreateUserRequest();
        req.setEmail("   ");
        req.setUsername("user");
        req.setPassword("password");
        req.setFirstName("F");
        req.setLastName("L");

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> usersService.createUser(req));
        assertEquals("Email is required", ex.getMessage());
    }

    @Test
    void createUser_blankUsername_throws() {
        CreateUserRequest req = new CreateUserRequest();
        req.setEmail("a@b.com");
        req.setUsername("   ");
        req.setPassword("password");
        req.setFirstName("F");
        req.setLastName("L");

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> usersService.createUser(req));
        assertEquals("Username is required", ex.getMessage());
    }

    @Test
    void createUser_blankPassword_throws() {
        CreateUserRequest req = new CreateUserRequest();
        req.setEmail("a@b.com");
        req.setUsername("user");
        req.setPassword("  ");
        req.setFirstName("F");
        req.setLastName("L");

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> usersService.createUser(req));
        assertEquals("Password is required", ex.getMessage());
    }

    @Test
    void createUser_nullRequest_throws() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> usersService.createUser(null));
        assertEquals("CreateUserRequest must not be null", ex.getMessage());
    }
}
