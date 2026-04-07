package com.example.examprepbackend.service.impl;

import com.example.examprepbackend.constant.ClassExamStatus;
import com.example.examprepbackend.constant.Role;
import com.example.examprepbackend.constant.Status;
import com.example.examprepbackend.dto.request.clazz.ClassRequest;
import com.example.examprepbackend.dto.request.clazz.ClassRequestParam;
import com.example.examprepbackend.dto.response.clazz.ClassDetailResponse;
import com.example.examprepbackend.dto.response.clazz.ClassResponse;
import com.example.examprepbackend.dto.response.exams.ExamSummaryResponse;
import com.example.examprepbackend.entity.ClassExam;
import com.example.examprepbackend.entity.Classes;
import com.example.examprepbackend.entity.Exam;
import com.example.examprepbackend.entity.Users;
import com.example.examprepbackend.exception.ApplicationException;
import com.example.examprepbackend.repository.*;
import com.example.examprepbackend.service.ClassService;
import com.example.examprepbackend.service.ClassTeacherService;
import com.example.examprepbackend.service.ExamService;
import com.example.examprepbackend.specification.ClassSpecification;
import com.example.examprepbackend.specification.ExamSpecification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static com.example.examprepbackend.constant.ClassExamStatus.HAS_EXAM;

@Slf4j
@Service
@RequiredArgsConstructor
public class ClassServiceImpl implements ClassService {

    private final ClassRepository classRepository;
    private final ModelMapper modelMapper;
    private final UsersRepository usersRepository;
    private final ClassTeacherRepository classTeacherRepository;
    private final ClassTeacherService classTeacherService;
    private final ExamService examService;
    private final ClassExamRepository classExamRepository;
    private final ExamRepository examRepository;

    private ClassResponse convertToDto(Classes classes) {
        ClassResponse classResponse = new ClassResponse();

        BeanUtils.copyProperties(classes, classResponse);
        classResponse.setStudentCount(usersRepository.countByRoleAndClasses_Id(Role.valueOf("STUDENT"), classes.getId()));
        classResponse.setTeacherCount(classTeacherRepository.countByClasses_Id(classes.getId()));

        return classResponse;
    }

    private ClassDetailResponse convertToDetailDto(Classes classes) {
        ClassDetailResponse classDetailResponse = new ClassDetailResponse();

        BeanUtils.copyProperties(classes, classDetailResponse);
        classDetailResponse.setStudentCount(usersRepository.countByRoleAndClasses_Id(Role.valueOf("STUDENT"), classes.getId()));

        //Set exam
        List<ExamSummaryResponse> examList = examService.getExamsByClassId(classes.getId());
        classDetailResponse.setExams(examList);

        return classDetailResponse;
    }

    @Override
    public Page<ClassResponse> getAllClasses(ClassRequestParam classRequestParam, Pageable pageable) {

        String name = classRequestParam.getName();
        LocalDate minDate = classRequestParam.getMinDate();
        LocalDate maxDate = classRequestParam.getMaxDate();

        Specification<Classes> spec = Specification.unrestricted();

        if (name != null && !name.isBlank()) {
            spec = spec.and(ClassSpecification.hasNameLike(name));
        }

        if (minDate != null) {
            spec = spec.and(ClassSpecification.hasAfterMinDate(minDate));
        }

        if (maxDate != null) {
            spec = spec.and(ClassSpecification.hasBeforeMinDate(maxDate));
        }

        if (minDate != null && maxDate != null) {
            spec = spec.and(ClassSpecification.hasCreateDate(minDate, maxDate));
        }

        return classRepository.findAll(spec, pageable).map(this::convertToDto);
    }

    @Override
    public Page<ClassDetailResponse> getClassesByTeacher(Authentication authentication,
                                                         Pageable pageable) {

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new ApplicationException("Unauthorized");
        }

        String username = authentication.getName();

        List<Integer> classIdList = classTeacherRepository.findByTeacher_Username(username);

        return classRepository.findByIdIn(classIdList, pageable).map(this::convertToDetailDto);
    }

    @Transactional
    @Override
    public ClassResponse createClass(ClassRequest classRequest) {

        Classes existsByName = classRepository.findByName(classRequest.getName());

        if (existsByName != null) {
            throw new ApplicationException("Class name existed");
        }

        Classes classes = new Classes();
        classes.setName(classRequest.getName());
        classes.setCreateDate(LocalDateTime.now());

        classRepository.save(classes);

        return modelMapper.map(classes, ClassResponse.class);
    }

    @Transactional
    @Override
    public ClassResponse updateClass(Integer id, ClassRequest classRequest) {

        Optional<Classes> classesOptional = classRepository.findById(id);

        if (classesOptional.isEmpty()) {
            throw new ApplicationException("Class not found");
        }

        Classes clazz = classesOptional.get();

        String currentName = clazz.getName();
        String newName = classRequest.getName();

        if (!newName.equals(currentName)) {
            Classes existsByName = classRepository.findByName(newName);

            if (existsByName != null) {
                throw new ApplicationException("Class name existed");
            }
        }

        clazz.setName(newName);
        classRepository.save(clazz);

        return modelMapper.map(clazz, ClassResponse.class);
    }

    @Transactional
    @Override
    public Boolean addStudentsToClass(Integer id, List<Integer> studentIdList) {
        Optional<Classes> classesOptional = classRepository.findById(id);
        if (classesOptional.isEmpty()) {
            throw new ApplicationException("Class not found");
        }

        List<Users> usersList = usersRepository.findByIdIn(studentIdList);
        for (Users student : usersList) {
            if (!"STUDENT".equals(student.getRole().toString())) {
                throw new ApplicationException("User: " + student.getUsername() + " not student");
            }
        }

        //Set ClassId = null
        usersRepository.updateClassIdToNull(id);

        //Set lại classId cho các học sinh trong danh sách
        if (studentIdList != null) {
            usersRepository.updateClassIdByIdIn(id, studentIdList);
        }

        return true;
    }

    @Transactional
    @Override
    public Boolean addTeachersToClass(Integer id, List<Integer> teacherIdList) {
        Optional<Classes> classesOptional = classRepository.findById(id);
        if (classesOptional.isEmpty()) {
            throw new ApplicationException("Class not found");
        }
        Classes classes = classesOptional.get();

        //Step1: Xóa toàn bộ giáo viên theo classId trong bảng trung gian class_teacher
        //Step2: Lưu lại dữ liệu theo class-teachers đã nhận vào bảng trung gian class_teacher
        classTeacherRepository.deleteByClasses_Id(id);

        if (teacherIdList != null) {
            List<Users> teacherList = usersRepository.findByIdIn(teacherIdList);
            for (Users teacher : teacherList) {
                if (!"TEACHER".equals(teacher.getRole().toString())) {
                    throw new ApplicationException("User: " + teacher.getUsername() + " not teacher");
                }
            }
            classTeacherService.createClassTeachers(classes, teacherList);
        }

        return true;
    }

    @Transactional
    @Override
    public Boolean addExamsToClass(Integer id, List<Integer> examIds) {
        Optional<Classes> classesOptional = classRepository.findById(id);
        if (classesOptional.isEmpty()) {
            throw new ApplicationException("Class not found");
        }
        Classes classes = classesOptional.get();

        //Step1: Xóa toàn bộ đề thi theo classId trong bảng trung gian class_exam
        //Step2: Lưu lại dữ liệu theo class-exams đã nhận vào bảng trung gian class_exam
        classExamRepository.deleteByClasses_Id(id);
        try {
            if (examIds != null) {
                List<Exam> examList = examRepository.findByIdIn(examIds);
                for (Exam exam : examList) {
                    ClassExam classExam = new ClassExam();

                    classExam.setClassId(id);
                    classExam.setExamId(exam.getId());

                    classExamRepository.save(classExam);
                }
            }

            return true;
        } catch (ApplicationException applicationException) {
            throw new ApplicationException("Lỗi lưu dữ liệu xuống bảng class-exam");
        }

    }

    @Transactional
    @Override
    public Boolean deleteById(Integer id) {
        Optional<Classes> classesOptional = classRepository.findById(id);

        if (classesOptional.isEmpty()) {
            throw new ApplicationException("Class not found");
        }

        classRepository.delete(classesOptional.get());

        return true;
    }

    @Override
    public long countClass() {
        return classRepository.count();
    }
}
