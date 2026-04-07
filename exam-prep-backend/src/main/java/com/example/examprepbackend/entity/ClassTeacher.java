package com.example.examprepbackend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "class_teacher")
public class ClassTeacher {
    @EmbeddedId
    private ClassTeacherId id;

    @ManyToOne
    @MapsId("classId")
    @JoinColumn(name = "class_id")
    private Classes classes;

    @ManyToOne
    @MapsId("teacherId")
    @JoinColumn(name = "teacher_id")
    private Users teacher;
}
