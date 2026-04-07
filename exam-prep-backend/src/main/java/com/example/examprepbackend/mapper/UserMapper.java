package com.example.examprepbackend.mapper;

import com.example.examprepbackend.dto.request.users.CreateUserRequest;
import com.example.examprepbackend.dto.response.users.UserSummaryResponse;
import com.example.examprepbackend.entity.Users;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {
    private final ModelMapper modelMapper;

    public UserMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    public Users toEntity(CreateUserRequest request) {
        if (request == null) return null;
        return modelMapper.map(request, Users.class);
    }

    public UserSummaryResponse toDto(Users user) {
        if (user == null) return null;
        return modelMapper.map(user, UserSummaryResponse.class);
    }
}
