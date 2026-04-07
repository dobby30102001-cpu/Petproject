package com.example.examprepbackend.dto.response.users;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Data
@AllArgsConstructor
public class UserInfoResponse {
    private Integer id;

    private String email;

    private String username;

    private String firstName;

    private String lastName;

    private String role;
}