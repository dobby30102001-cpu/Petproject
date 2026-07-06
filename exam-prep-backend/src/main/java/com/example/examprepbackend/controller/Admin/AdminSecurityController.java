package com.example.examprepbackend.controller.Admin;

import com.example.examprepbackend.common.BaseResponse;
import com.example.examprepbackend.service.AuthenticationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@Slf4j
@RequestMapping("/api/v1/admin/security")
public class AdminSecurityController {
    private final AuthenticationService authenticationService;

    @PutMapping("/account/unlock/{id}")
    public ResponseEntity<BaseResponse<String>> unlock(@PathVariable Integer id ) {
        String email = authenticationService.unlockAccount(id);
        return ResponseEntity.ok(new BaseResponse<>( "email :" + email,"Mở khóa thành công" ));
    }
    @PutMapping("/account/lock/{id}")
    public ResponseEntity<BaseResponse<String>> lock(@PathVariable Integer id) {
        String email = authenticationService.lockAccount(id);
        return ResponseEntity.ok(
                new BaseResponse<>("email: " + email, "Khóa tài khoản thành công")
        );
    }

}
