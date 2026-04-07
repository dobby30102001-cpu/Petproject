package com.example.examprepbackend.dto.authentication;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class TestPass {
    public static void main(String[] args) {
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        System.out.println(bCryptPasswordEncoder.encode("admin123"));//ADMIN
        System.out.println(bCryptPasswordEncoder.encode("12345"));//TEACHER
        System.out.println(bCryptPasswordEncoder.encode("1234"));//STUDENT
    }
}
