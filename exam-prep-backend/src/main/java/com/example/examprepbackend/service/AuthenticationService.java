package com.example.examprepbackend.service;

import com.example.examprepbackend.dto.authentication.ForgotPassword;
import com.example.examprepbackend.dto.authentication.LoginRequest;
import com.example.examprepbackend.dto.authentication.LoginResponse;
import com.example.examprepbackend.dto.authentication.ResetPassword;

import java.net.http.HttpRequest;


public interface AuthenticationService {
    // admin
    String unlockAccount(Integer userId );

    String lockAccount(Integer userId );

    // user
    LoginResponse login(LoginRequest loginRequest );

    String sendOtp(ForgotPassword forgetpw);

    Boolean resetPassword(ResetPassword resetpw);

}
