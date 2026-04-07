package com.example.examprepbackend.dto.response.users;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class UserSummaryResponse {
    private String email;

    private String username;

    private String firstName;

    private String lastName;

}
