package com.example.examprepbackend.common;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class BaseResponse<T> {
    private T data;
    private String message;

    public static <T> BaseResponse<T> success(T data) {
        return new BaseResponse<>(data, "SUCCESS");
    }


    public static <T> BaseResponse<T> error(String message) {
        return new BaseResponse<>(null, message);
    }
}