package com.example.examprepbackend.dto.response.users;

import com.example.examprepbackend.constant.Role;
import lombok.*;
import com.example.examprepbackend.constant.Status;
import com.example.examprepbackend.entity.Classes;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Getter
@Builder
@Setter
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserResponse {

    private Integer id;

    private String email;
    private String fullName;

    private String username;

    private String firstName;

    private String lastName;

    @Enumerated(EnumType.STRING)
    private Role role;

    private Boolean isActive;

    @Enumerated(EnumType.STRING)
    private Status status;

    private Classes classes;

    private LocalDateTime createdDate;


}
