package com.example.examprepbackend.dto.request.users;

import com.example.examprepbackend.constant.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateUserRequest {

    @Email
    @NotBlank(message = "Email must not be blank")
    private String email;

    @NotBlank(message = "UserName must not be blank")
    private String username;

    @NotBlank(message = "ROlE must not be blank")
    private String role;

    @NotBlank(message = "fullName must not be blank")
    private String fullName;



}
