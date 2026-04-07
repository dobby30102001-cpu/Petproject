package com.example.examprepbackend.repository;

import com.example.examprepbackend.entity.Otp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface OtpRepository extends JpaRepository<Otp,Integer> {

    Otp findByEmailAndOtp(String email, Integer otp);

    // xóa otp cũ theo email
    void deleteByEmail(String email);

//lấy otp mới nhất theo mail
    Optional<Otp> findTopByEmailOrderByCreatedAtDesc(String email);

}
