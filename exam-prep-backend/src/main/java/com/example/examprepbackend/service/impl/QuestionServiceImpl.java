package com.example.examprepbackend.service.impl;

import com.example.examprepbackend.config.SecurityUtils;
import com.example.examprepbackend.constant.DifficultyLevel;
import com.example.examprepbackend.constant.Role;
import com.example.examprepbackend.dto.request.exams.CheckAnswerRequest;
import com.example.examprepbackend.dto.request.teacher.Question.CreateAnswerRequest;
import com.example.examprepbackend.dto.request.teacher.Question.CreateQuestionRequest;
import com.example.examprepbackend.dto.request.teacher.Question.QuestionRequestParam;
import com.example.examprepbackend.dto.response.answer.AnswerPublicResponse;
import com.example.examprepbackend.dto.response.questions.QuestionPublicResponse;
import com.example.examprepbackend.dto.response.exams.CheckAnswerResponse;
import com.example.examprepbackend.dto.response.teacher.AnswerResponse;
import com.example.examprepbackend.dto.response.questions.QuestionResponse;
import com.example.examprepbackend.entity.*;
import com.example.examprepbackend.dto.response.teacher.QuestionCountResponse;
import com.example.examprepbackend.exception.ApplicationException;
import com.example.examprepbackend.repository.*;
import com.example.examprepbackend.repository.AnswerRepository;
import com.example.examprepbackend.repository.CategoryQuestionRepository;
import com.example.examprepbackend.repository.QuestionRepository;
import com.example.examprepbackend.repository.UsersRepository;
import com.example.examprepbackend.service.QuestionService;
import com.example.examprepbackend.specification.QuestionSpecification;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.User;
import org.apache.poi.ss.usermodel.*;
import org.modelmapper.ModelMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Slf4j
@Service
@RequiredArgsConstructor
public class QuestionServiceImpl implements QuestionService {

    private final QuestionRepository questionRepository;
    private final AnswerRepository answerRepository;
    private final UsersRepository userRepository;
    private final CategoryQuestionRepository categoryRepository;
    private final ExamQuestionRepository examQuestionRepository;
    private final ClassRepository classRepository;
    private final ClassExamRepository classExamRepository;
    private final ExamRepository examRepository;


    //Map question -> questionResponse
    private QuestionResponse convertToDto(Question question) {

        QuestionResponse questionResponse = new QuestionResponse();

        BeanUtils.copyProperties(question, questionResponse);

        questionResponse.setCategory(question.getCategory().getName());

        questionResponse.setDifficulty(question.getDifficultyLevel());

        questionResponse.setCreatedDate(
                question.getCreateDate().toLocalDate().toString()
        );

        // lấy answers
        if (question.getAnswers() != null) {
            List<AnswerResponse> answerResponses = question.getAnswers().stream().map(a -> {
                AnswerResponse dto = new AnswerResponse();
                dto.setContent(a.getContent());
                dto.setIsCorrect(a.getIsCorrect());
                return dto;
            }).toList();

            questionResponse.setAnswers(answerResponses);
        }
        questionResponse.setCreator(question.getCreator().getUsername());

        return questionResponse;
    }

    private QuestionPublicResponse convertToPublicDto(Question question) {

        QuestionPublicResponse questionPublicResponse = new QuestionPublicResponse();

        BeanUtils.copyProperties(question, questionPublicResponse);

        //Lay danh sach answer
        if (question.getAnswers() != null) {
            List<AnswerPublicResponse> answerPublicResponses = question.getAnswers().stream().map(answer -> {
                AnswerPublicResponse answerPublicResponse = new AnswerPublicResponse();
                answerPublicResponse.setId(answer.getId());
                answerPublicResponse.setContent(answer.getContent());
                answerPublicResponse.setLabel(answer.getLabel());
                return answerPublicResponse;
            }).toList();

            questionPublicResponse.setAnswers(answerPublicResponses);
        }

        return questionPublicResponse;
    }


    @Override
    public Page<QuestionResponse> getAllQuestions(QuestionRequestParam param, Pageable pageable) {

        String content = param.getContent();
        DifficultyLevel difficulty = param.getDifficulty();
        Integer categoryId = param.getCategoryId();
        Integer creatorId = param.getCreatorId();
        LocalDate minDate = param.getMinDate();
        LocalDate maxDate = param.getMaxDate();

        Specification<Question> spec = Specification.unrestricted();

        if (content != null && !content.isBlank()) {
            spec = spec.and(QuestionSpecification.hasContentLike(content));
        }

        if (difficulty != null) {
            spec = spec.and(QuestionSpecification.hasDifficulty(difficulty));
        }

        if (categoryId != null) {
            spec = spec.and(QuestionSpecification.hasCategoryId(categoryId));
        }

        if (creatorId != null) {
            spec = spec.and(QuestionSpecification.hasCreatorId(creatorId));
        }

        if (minDate != null && maxDate != null) {
            spec = spec.and(QuestionSpecification.hasCreateDate(minDate, maxDate));
        }

        return questionRepository.findAll(spec, pageable)
                .map(this::convertToDto);
    }

    @Override
    public Page<QuestionResponse> getMyQuestions(
            QuestionRequestParam param,
            Pageable pageable
    ) {

        String username = SecurityUtils.getCurrentUsername();

        if (username == null) {
            throw new ApplicationException("User not logged in");
        }

        String content = param.getContent();
        DifficultyLevel difficulty = param.getDifficulty();
        Integer categoryId = param.getCategoryId();
        LocalDate minDate = param.getMinDate();
        LocalDate maxDate = param.getMaxDate();

        Specification<Question> spec = QuestionSpecification.hasCreatorUsername(username);

        if (content != null && !content.isBlank()) {
            spec = spec.and(QuestionSpecification.hasContentLike(content));
        }

        if (difficulty != null) {
            spec = spec.and(QuestionSpecification.hasDifficulty(difficulty));
        }

        if (categoryId != null) {
            spec = spec.and(QuestionSpecification.hasCategoryId(categoryId));
        }

        if (minDate != null && maxDate != null) {
            spec = spec.and(QuestionSpecification.hasCreateDate(minDate, maxDate));
        }

        return questionRepository.findAll(spec, pageable)
                .map(this::convertToDto);
    }

    @Override
    public QuestionResponse getQuestionById(Integer id) {
        Optional<Question> question = questionRepository.findById(id);
        if (question.isEmpty()) {
            throw new ApplicationException("Question with id " + id + " not found");
        }
        return convertToDto(question.get());
    }

    @Override
    public List<QuestionResponse> getQuestionsByExamId(Integer examId) {

        List<Integer> questionIds = examQuestionRepository.findQuestionsByExamId(examId);

        return questionRepository.findByIdIn(questionIds).stream().map(this::convertToDto).toList();
    }

    @Override
    public List<QuestionPublicResponse> getQuestionsPublicByExamId(Integer examId) {

        //Kiem tra exam
        Optional<Exam> examOptional = examRepository.findById(examId);
        if (examOptional.isEmpty()) {
            throw new ApplicationException("Exam not found");
        }

        List<Integer> questionIds = examQuestionRepository.findQuestionsByExamId(examId);

        return questionRepository.findByIdIn(questionIds).stream().map(this::convertToPublicDto).toList();
    }

    @Transactional
    @Override
    public QuestionResponse createQuestion(CreateQuestionRequest request) {

        Question question = new Question();

        question.setContent(request.getContent());
        question.setDifficultyLevel(request.getDifficulty());

        Optional<CategoryQuestion> categoryQuestion = categoryRepository.findById(request.getCategoryId());
        if (categoryQuestion.isEmpty()) {
            throw new ApplicationException("Category with id " + request.getCategoryId() + " not found");
        }
        question.setCategory(categoryQuestion.get());

        String username = SecurityUtils.getCurrentUsername();


        Optional<Users> user = userRepository.findByUsername(username);
        if (user.isEmpty()) {
            throw new ApplicationException("User with name " + username + " not found");
        }
        question.setCreator(user.get());

        question.setCreateDate(LocalDateTime.now());

        Question savedQuestion = questionRepository.save(question);

        List<Answer> answers = new ArrayList<>();

        for (CreateAnswerRequest a : request.getAnswers()) {

            Answer answer = new Answer();
            answer.setContent(a.getContent());
            answer.setIsCorrect(a.getIsCorrect());
            answer.setQuestion(savedQuestion);

            answers.add(answer);
        }

        answerRepository.saveAll(answers);

        return convertToDto(savedQuestion);
    }

    @Transactional
    @Override
    public QuestionResponse updateQuestion(Integer id, CreateQuestionRequest request) {

        Optional<Question> question = questionRepository.findById(id);
        if (question.isEmpty()) {
            throw new ApplicationException("Question with id " + id + " not found");
        }
        Question questions = question.get();
        // update question
        questions.setContent(request.getContent());
        questions.setDifficultyLevel(request.getDifficulty());

        Optional<CategoryQuestion> categoryQuestion = categoryRepository.findById(request.getCategoryId());
        if (categoryQuestion.isEmpty()) {
            throw new ApplicationException("Category with id " + request.getCategoryId() + " not found");
        }

        questions.setCategory(categoryQuestion.get());

        Question savedQuestion = questionRepository.save(questions);

        // xóa answers cũ
        answerRepository.deleteByQuestion_Id(id);

        // thêm answers mới
        List<Answer> answers = new ArrayList<>();

        for (CreateAnswerRequest a : request.getAnswers()) {

            Answer answer = new Answer();
            answer.setContent(a.getContent());
            answer.setIsCorrect(a.getIsCorrect());
            answer.setQuestion(savedQuestion);

            answers.add(answer);
        }

        answerRepository.saveAll(answers);

        return convertToDto(savedQuestion);
    }

    @Transactional
    @Override
    public void deleteQuestion(Integer id) {

        Optional<Question> question = questionRepository.findById(id);
        if (question.isEmpty()) {
            throw new ApplicationException("Question with id " + id + " not found");
        }

        questionRepository.delete(question.get());
    }

    @Override
    public void exportQuestionToExcel(HttpServletResponse response) throws IOException {

        List<Question> questions = questionRepository.findAll();

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Questions");

        // ===== HEADER STYLE =====
        CellStyle headerStyle = workbook.createCellStyle();
        Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerStyle.setFont(headerFont);

        // ===== HEADER =====
        Row headerRow = sheet.createRow(0);

        String[] columns = {
                "ID",
                "Content",
                "Difficulty",
                "Category",
                "Answer1",
                "Correct1",
                "Answer2",
                "Correct2",
                "Answer3",
                "Correct3",
                "Answer4",
                "Correct4",
                "Explain",
                "Created Date"
        };

        for (int i = 0; i < columns.length; i++) {

            Cell cell = headerRow.createCell(i);

            cell.setCellValue(columns[i]);

            cell.setCellStyle(headerStyle);
        }

        // ===== DATA =====

        int rowIndex = 1;

        for (Question question : questions) {

            Row row = sheet.createRow(rowIndex++);

            row.createCell(0).setCellValue(question.getId());
            row.createCell(1).setCellValue(question.getContent());

            row.createCell(2).setCellValue(
                    question.getDifficultyLevel() != null ?
                            question.getDifficultyLevel().toString() : ""
            );

            row.createCell(3).setCellValue(
                    question.getCategory() != null ?
                            question.getCategory().getName() : ""
            );

            List<Answer> answers = question.getAnswers();

            for (int j = 0; j < 4; j++) {

                if (answers != null && j < answers.size()) {

                    row.createCell(4 + j * 2)
                            .setCellValue(answers.get(j).getContent());

                    row.createCell(5 + j * 2)
                            .setCellValue(answers.get(j).getIsCorrect());
                }
            }

            // Explain
            row.createCell(12).setCellValue(
                    question.getExplanation() != null ? question.getExplanation() : ""
            );

            // Created date
            row.createCell(13).setCellValue(
                    question.getCreateDate() != null ?
                            question.getCreateDate().toString() : ""
            );
        }

        // ===== AUTO SIZE =====

        for (int i = 0; i < columns.length; i++) {
            sheet.autoSizeColumn(i);
        }

        // ===== WRITE FILE =====

        ServletOutputStream outputStream = response.getOutputStream();

        workbook.write(outputStream);
        workbook.close();

        outputStream.close();
    }

    @Override
    @Transactional
    public void importQuestionFromExcel(MultipartFile file) throws IOException {
        if (!file.getOriginalFilename().endsWith(".xlsx")) {
            throw new ApplicationException("Only Excel file allowed");
        }
        Workbook workbook = new XSSFWorkbook(file.getInputStream());

        Sheet sheet = workbook.getSheetAt(0);

        for (int i = 1; i <= sheet.getLastRowNum(); i++) {

            Row row = sheet.getRow(i);

            if (row == null) continue;

            Question question = new Question();

            question.setContent(row.getCell(0).getStringCellValue());

            question.setDifficultyLevel(
                    DifficultyLevel.valueOf(row.getCell(1).getStringCellValue())
            );

            Integer categoryId = (int) row.getCell(2).getNumericCellValue();

            CategoryQuestion category = categoryRepository
                    .findById(categoryId)
                    .orElseThrow(() -> new ApplicationException("Category not found"));

            question.setCategory(category);
            question.setExplanation(row.getCell(11).getStringCellValue());
            String username = SecurityUtils.getCurrentUsername();

            Users user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new ApplicationException("User not found"));

            question.setCreator(user);

            question.setCreateDate(LocalDateTime.now());

            Question savedQuestion = questionRepository.save(question);

            List<Answer> answers = new ArrayList<>();

            for (int j = 0; j < 4; j++) {

                Answer answer = new Answer();

                answer.setContent(row.getCell(3 + j * 2).getStringCellValue());

                answer.setIsCorrect(row.getCell(4 + j * 2).getBooleanCellValue());

                answer.setQuestion(savedQuestion);

                answers.add(answer);
            }

            answerRepository.saveAll(answers);
        }

        workbook.close();
    }

    @Override
    public QuestionCountResponse getAllQuestionsCount() {
        String username = SecurityUtils.getCurrentUsername();

        if (username == null) {
            throw new ApplicationException("User not logged in");
        }

        QuestionCountResponse response = new QuestionCountResponse();

        response.setCountTotal(
                questionRepository.countByCreator_Username(username)
        );

        response.setCountEasy(
                questionRepository.countByCreator_UsernameAndDifficultyLevel(
                        username, DifficultyLevel.EASY
                )
        );

        response.setCountMedium(
                questionRepository.countByCreator_UsernameAndDifficultyLevel(
                        username, DifficultyLevel.MEDIUM
                )
        );

        response.setCountHard(
                questionRepository.countByCreator_UsernameAndDifficultyLevel(
                        username, DifficultyLevel.HARD
                )
        );

        return response;

    }

    //student
    @Override
    public Page<QuestionResponse> getAllQuestionsByStudent(Pageable pageable) {
        String username = SecurityUtils.getCurrentUsername();
        if (username == null) return Page.empty(pageable);

        Users student = userRepository.findByUsername(username)
                .orElse(null);
        if (student == null || student.getClasses() == null) return Page.empty(pageable);

        Integer classId = student.getClasses().getId();
        List<Integer> examIds = classExamRepository.findByClassId(classId);
        if (examIds.isEmpty()) return Page.empty(pageable);

        Page<Question> questionsPage = questionRepository
                .findQuestionsByExamIdsAndCreatorRole(examIds, Role.TEACHER, pageable);

        return questionsPage.map(this::convertToDto);


    }

    @Override
    public CheckAnswerResponse checkAnswer(CheckAnswerRequest request) {
        // kiểm tra xem câu hỏi có tồn tại không
        Question question = questionRepository.findById(request.getQuestionId())
                .orElseThrow(() -> new ApplicationException("Question not found: " + request.getQuestionId()));

        //lấy danh sách đáp án của câu hỏi
        List<Answer> answers = answerRepository.findByQuestion_Id(request.getQuestionId());
        if (answers.isEmpty()) {
            throw new ApplicationException("No answer found for question: " + request.getQuestionId());
        }

        // tìm đáp án đúng (isCorrect = true)
        Answer correctAnswer = answers.stream()
                .filter(Answer::getIsCorrect)
                .findFirst()
                .orElseThrow(() -> new ApplicationException("No correct answer found for question: " + request.getQuestionId()));

        // kiểm tra đáp án
        boolean isCorrect = correctAnswer.getId().equals(request.getSelectedAnswerId());

        // trả về kết quả id đáp án đúng và giải thích
        return CheckAnswerResponse.builder()
                .isCorrect(isCorrect)
                .correctAnswerId(correctAnswer.getId())
                .explanation(question.getExplanation())
                .build();
    }
}
