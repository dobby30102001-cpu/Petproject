package com.example.examprepbackend.entity;

import com.example.examprepbackend.constant.Role;
import com.example.examprepbackend.constant.Status;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name="users")
@JsonIgnoreProperties({"hibernateLazyInitializer","handler"})
public class Users {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Integer id;

    private String email;

    private String username;

    @JsonIgnore
    private String password;

    @Column(name="first_name")
    private String firstName;

    @Column(name="last_name")
    private String lastName;

    @Enumerated(EnumType.STRING)
    private Role role;

    @Column(name="is_active")
    private Boolean isActive;

    @Enumerated(EnumType.STRING)
    private Status status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "class_id")
    private Classes classes;

    @Column(name = "create_date")
    private LocalDateTime createdDate;
// thêm cột này để đếm số lần bị khóa
    @Column(name = "fail_count")
    private Integer failCount;
// hiển thị thời gian bị khóa
    @Column(name = "lock_time")
    private LocalDateTime lockTime;

}
