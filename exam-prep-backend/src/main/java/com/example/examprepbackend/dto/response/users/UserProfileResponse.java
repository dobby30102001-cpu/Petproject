package com.example.examprepbackend.dto.response.users;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserProfileResponse {
    private Integer id;

    private String email;

    private String username;

    private String firstName;

    private String lastName;

    private String role;

    private Integer classId;

    private String className;
}
