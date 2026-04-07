package com.example.examprepbackend.dto.authentication;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResetPassword {

    private String email;

    @NotNull(message = "otp not null")
    private Integer otp;

    @NotNull(message = "new Password not null")
    private String newPassword;

    @NotNull(message = " confirm new Password not null")
    private String confirmNewPassword;

}
