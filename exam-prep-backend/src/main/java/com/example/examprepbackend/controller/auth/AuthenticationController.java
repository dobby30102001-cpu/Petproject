package com.example.examprepbackend.controller.auth;

import com.example.examprepbackend.common.BaseResponse;
import com.example.examprepbackend.dto.authentication.ForgotPassword;
import com.example.examprepbackend.dto.authentication.LoginRequest;
import com.example.examprepbackend.dto.authentication.LoginResponse;
import com.example.examprepbackend.dto.authentication.ResetPassword;
import com.example.examprepbackend.service.AuthenticationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;



@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthenticationController {

    private final AuthenticationService authenticationService;


    @PostMapping("/login")
    public ResponseEntity<BaseResponse< LoginResponse>> login(@RequestBody LoginRequest loginRequest){
        return ResponseEntity.ok(new BaseResponse<>(
                authenticationService.login(loginRequest) ,
                "Login Succesfull"
        ));
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<BaseResponse<String>> forgotPassWork(
            @RequestBody @Valid ForgotPassword forgetpw) {

        String result = authenticationService.sendOtp(forgetpw);

        return ResponseEntity.ok(new BaseResponse<>(
                result,
                "Regain password success"
        ));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<BaseResponse<Boolean>> resetPassword(@RequestBody ResetPassword resetpw) {
        Boolean result = authenticationService.resetPassword(resetpw);
        return ResponseEntity.ok(new BaseResponse<>(result, "Reset password successful!"));
    }
}