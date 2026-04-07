package com.example.examprepbackend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync// tăng tốc độ gửi mail
public class ExamPrepBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(ExamPrepBackendApplication.class, args);
    }

}
