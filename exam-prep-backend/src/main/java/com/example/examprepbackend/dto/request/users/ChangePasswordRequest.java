package com.example.examprepbackend.dto.request.users;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChangePasswordRequest {

    @NotNull(message = "Password not null")
    @NotBlank(message = "Password not blank")
    private String password;

    @NotNull(message = "New password not null")
    @NotBlank(message = "New password not blank")
    private String newPassword;
}
