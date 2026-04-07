package com.example.examprepbackend.dto.request.clazz;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ClassRequest {

    @NotNull(message = "Class name not null")
    @NotBlank(message = "Class name not blank")
    private String name;

}
