package com.example.examprepbackend.dto.request.users;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserProfileUpdateRequest {

    @NotNull(message = "email not null")
    @NotBlank(message = "email not blank")
    private String email;

    @NotNull(message = "First name not null")
    @NotBlank(message = "First name not blank")
    private String firstName;

    @NotNull(message = "Last name not null")
    @NotBlank(message = "last name not blank")
    private String lastName;
}