package com.example.examprepbackend.service.impl;

import com.example.examprepbackend.constant.ExamType;
import com.example.examprepbackend.dto.request.exams.ExamCreateRequest;
import com.example.examprepbackend.dto.request.exams.ExamRequestParam;
import com.example.examprepbackend.dto.request.exams.ExamUpdateRequest;
import com.example.examprepbackend.dto.response.exams.ExamAttemptResponse;
import com.example.examprepbackend.dto.response.exams.ExamResponse;
import com.example.examprepbackend.dto.response.exams.ExamSummaryResponse;
import com.example.examprepbackend.dto.response.users.StudentResponse;
import com.example.examprepbackend.entity.*;
import com.example.examprepbackend.exception.ApplicationException;
import com.example.examprepbackend.repository.*;
import com.example.examprepbackend.service.ExamQuestionService;
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
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;


@Slf4j
@Service
@RequiredArgsConstructor
public class ExamServiceImpl implements ExamService {

    private final ExamRepository examRepository;
    private final ExamQuestionRepository examQuestionRepository;
    private final ExamAttemptRepository examAttemptRepository;
    private final UsersRepository usersRepository;
    private final CategoryQuestionRepository categoryQuestionRepository;
    private final QuestionRepository questionRepository;
    private final ClassRepository classRepository;
    private final ClassExamRepository classExamRepository;

    private final ExamQuestionService examQuestionService;
    private final ModelMapper modelMapper;

    private ExamResponse convertToDto(Exam exam) {

        ExamResponse examResponse = new ExamResponse();

        BeanUtils.copyProperties(exam, examResponse);
        examResponse.setCategory(exam.getCategory().getName());
        examResponse.setCreatorName(exam.getCreator().getUsername());
        examResponse.setQuestions(examQuestionRepository.countByExam_Id(exam.getId()));
        examResponse.setAttempts(examAttemptRepository.countByExam_Id(exam.getId()));

        return examResponse;
    }

    private ExamAttemptResponse convertToExamAttempt(ExamAttempt examAttempt) {
        ExamAttemptResponse examAttemptResponse = new ExamAttemptResponse();

        BeanUtils.copyProperties(examAttempt, examAttemptResponse);

        StudentResponse studentResponse = new StudentResponse();
        BeanUtils.copyProperties(examAttempt.getStudent(), studentResponse);
        examAttemptResponse.setStudent(studentResponse);

        ExamSummaryResponse examSummaryResponse = new ExamSummaryResponse();
        BeanUtils.copyProperties(examAttempt.getExam(), examSummaryResponse);
        examAttemptResponse.setExam(examSummaryResponse);

        return examAttemptResponse;

    }

    private Specification<Exam> buildExamFilter(ExamRequestParam requestParam) {
        Specification<Exam> spec = Specification.unrestricted();

        if (requestParam == null) {
            return spec;
        }

        String code = requestParam.getCode();
        String title = requestParam.getTitle();
        String categoryName = requestParam.getCategoryName();
        LocalDate minDate = requestParam.getMinDate();
        LocalDate maxDate = requestParam.getMaxDate();

        if (code != null && !code.isBlank()) {
            spec = spec.and(ExamSpecification.hasCodeLike(code));
        }

        if (title != null && !title.isBlank()) {
            spec = spec.and(ExamSpecification.hasTitleLike(title));
        }

        if (categoryName != null && !categoryName.isBlank()) {
            spec = spec.and(ExamSpecification.hasCategoryName(categoryName));
        }

        if (minDate != null && maxDate != null) {
            spec = spec.and(ExamSpecification.hasCreateDate(minDate, maxDate));
        } else {
            if (minDate != null) {
                spec = spec.and(ExamSpecification.hasAfterMinDate(minDate));
            }
            if (maxDate != null) {
                spec = spec.and(ExamSpecification.hasBeforeMaxDate(maxDate));
            }
        }
        return spec;
    }


    @Override
    public Page<ExamResponse> getAllExams(ExamRequestParam examRequestParam, Pageable pageable) {

        Specification<Exam> spec = buildExamFilter(examRequestParam);

        return examRepository.findAll(spec, pageable).map(this::convertToDto);
    }

    @Override
    public Page<ExamResponse> getExamsByTeacherName(Authentication authentication, ExamRequestParam examRequestParam, Pageable pageable) {

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new ApplicationException("Unauthorized");
        }

        String username = authentication.getName();

        Specification<Exam> spec = Specification
                .where(ExamSpecification.hasCreatorUsername(username))
                .and(buildExamFilter(examRequestParam));

        return examRepository.findAll(spec, pageable).map(this::convertToDto);
    }

    @Override
    public Page<ExamAttemptResponse> getExamAttemptsByTeacher(Authentication authentication, Pageable pageable) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new ApplicationException("Unauthorized");
        }

        String teacherUsername = authentication.getName();
        List<Exam> examList = examRepository.findExamsByCreator_Username(teacherUsername);

        return examAttemptRepository.findByExamIn(examList, pageable).map(this::convertToExamAttempt);
    }

    @Override
    public List<ExamSummaryResponse> getExamsByClassId(Integer classId) {
        Optional<Classes> classesOptional = classRepository.findById(classId);
        if (classesOptional.isEmpty()) {
            throw new ApplicationException("Class not found");
        }

        List<Integer> examIdList = classExamRepository.findByClassId(classId);

        return examRepository.findByIdIn(examIdList).stream().map(e -> modelMapper.map(e, ExamSummaryResponse.class)).toList();
    }

    @Override
    public Page<ExamResponse> getPracticeExamsByClassId(Integer classId, ExamRequestParam examRequestParam, Pageable pageable) {

        Optional<Classes> classesOptional = classRepository.findById(classId);
        if (classesOptional.isEmpty()) {
            throw new ApplicationException("Class not found");
        }

        List<Integer> examIdList = classExamRepository.findByClassId(classId);

        Specification<Exam> spec = Specification
                .where(ExamSpecification.hasIdIn(examIdList))
                .and(ExamSpecification.hasExamType(ExamType.PRACTICE))
                .and(buildExamFilter(examRequestParam));

        return examRepository.findAll(spec, pageable).map(this::convertToDto);

    }

    @Override
    public Page<ExamResponse> getOfficialExamsByClassId(Integer classId, ExamRequestParam examRequestParam, Pageable pageable) {
        Optional<Classes> classesOptional = classRepository.findById(classId);
        if (classesOptional.isEmpty()) {
            throw new ApplicationException("Class not found");
        }

        List<Integer> examIdList = classExamRepository.findByClassId(classId);

        Specification<Exam> spec = Specification
                .where(ExamSpecification.hasIdIn(examIdList))
                .and(ExamSpecification.hasExamType(ExamType.OFFICIAL))
                .and(buildExamFilter(examRequestParam));

        return examRepository.findAll(spec, pageable).map(this::convertToDto);

    }

    @Transactional
    @Override
    public ExamSummaryResponse createExam(Authentication authentication, ExamCreateRequest examCreateRequest) {

        //Check creator
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new ApplicationException("Unauthorized");
        }
        Optional<Users> optionalUsers = usersRepository.findByUsername(authentication.getName());
        if (optionalUsers.isEmpty()) {
            throw new ApplicationException("User not found");
        }

        //Check code
        Exam exitsByCode = examRepository.findByCode(examCreateRequest.getCode());
        if (exitsByCode != null) {
            throw new ApplicationException("Exam code exited");
        }

        //Check category
        CategoryQuestion category = categoryQuestionRepository.findByName(examCreateRequest.getCategory());
        if (category == null) {
            throw new ApplicationException("Category not found");
        }

        //Check số lượng câu hỏi với đề thi thật
        if ("OFFICIAL".equals(examCreateRequest.getExamType())) {
            validateLimitQuestion(examCreateRequest.getDuration(), examCreateRequest.getQuestionIds().size());
        }

        //Save exam
        Users creator = optionalUsers.get();

        Exam newExam = modelMapper.map(examCreateRequest, Exam.class);
        newExam.setCreator(creator);
        newExam.setCategory(category);
        newExam.setExamType(ExamType.valueOf(examCreateRequest.getExamType()));
        newExam.setCreateDate(LocalDateTime.now());
        newExam.setIsActive(true);
        examRepository.save(newExam);

        //Save exam-questions
        List<Integer> questionIds = examCreateRequest.getQuestionIds();
        if (questionIds != null) {
            List<Question> questionList = questionRepository.findByIdIn(examCreateRequest.getQuestionIds());
            examQuestionService.createExamQuestions(newExam, questionList);
        }

        return modelMapper.map(newExam, ExamSummaryResponse.class);
    }


    @Transactional
    @Override
    public ExamSummaryResponse updateExamById(Integer id, ExamUpdateRequest examUpdateRequest) {
        //Check exam by id
        Optional<Exam> examOptional = examRepository.findById(id);
        if (examOptional.isEmpty()) {
            throw new ApplicationException("Exam not found");
        }
        Exam exam = examOptional.get();

        //Check code
        String currentCode = exam.getCode();
        String newCode = examUpdateRequest.getCode();

        if (!newCode.equals(currentCode)) {
            Exam exitsByCode = examRepository.findByCode(newCode);
            if (exitsByCode != null) {
                throw new ApplicationException("Exam code exited");
            }
        }

        //Check số lượng câu hỏi với đề thi thật
        if ("OFFICIAL".equals(examUpdateRequest.getExamType())) {
            validateLimitQuestion(examUpdateRequest.getDuration(), examUpdateRequest.getQuestionIds().size());
        }

        //Save exam
        modelMapper.map(examUpdateRequest, exam);
        exam.setExamType(ExamType.valueOf(examUpdateRequest.getExamType()));
        examRepository.save(exam);

        //Update questions
        //Step1: Xóa toàn bộ câu hỏi theo examId trong bảng trung gian exam_question
        //Step2: Lưu lại dữ liệu theo exam-questions đã nhận vào bảng trung gian exam_question
        examQuestionRepository.deleteByExam_Id(id);

        List<Integer> questionIds = examUpdateRequest.getQuestionIds();
        if (questionIds != null) {
            List<Question> questionList = questionRepository.findByIdIn(examUpdateRequest.getQuestionIds());
            examQuestionService.createExamQuestions(exam, questionList);
        }

        return modelMapper.map(exam, ExamSummaryResponse.class);
    }

    //Check số lượng câu hỏi tối đa trong 1 đề thi theo thời gian
    private void validateLimitQuestion(LocalTime duration, Integer questionCount) {
        Integer maxQuestion;

        int examMinutesTime = duration.getHour() * 60 + duration.getMinute();

        if (examMinutesTime <= 15) {
            maxQuestion = 10;
        } else if (examMinutesTime <= 30) {
            maxQuestion = 15;
        } else if (examMinutesTime <= 45) {
            maxQuestion = 30;
        } else {
            maxQuestion = examMinutesTime * 2 / 3;
        }

        if (questionCount > maxQuestion) {
            throw new ApplicationException("Số lượng câu hỏi đã vượt quá giới hạn. Số câu hỏi tối đa cho phép: " + maxQuestion);
        }

    }

    @Transactional
    @Override
    public Boolean deleteExamById(Integer id) {
        Optional<Exam> examOptional = examRepository.findById(id);
        if (examOptional.isEmpty()) {
            throw new ApplicationException("Exam not found");
        }

        examRepository.deleteById(id);

        return true;
    }
}
