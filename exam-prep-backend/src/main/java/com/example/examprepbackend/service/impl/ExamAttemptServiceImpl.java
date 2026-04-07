package com.example.examprepbackend.service.impl;

import com.example.examprepbackend.config.SecurityUtils;
import com.example.examprepbackend.constant.AttemptStatus;
import com.example.examprepbackend.constant.ExamType;
import com.example.examprepbackend.dto.request.exams.CheckAnswerRequest;
import com.example.examprepbackend.dto.request.exams.ExamTypeRequest;
import com.example.examprepbackend.dto.request.exams.SubmitAnswerRequest;
import com.example.examprepbackend.dto.request.exams.SubmitExamAttemptRequest;
import com.example.examprepbackend.dto.response.exams.*;
import com.example.examprepbackend.dto.response.questions.AttemptQuestionOptionResponse;
import com.example.examprepbackend.dto.response.questions.AttemptQuestionResponse;
import com.example.examprepbackend.dto.response.questions.AttemptQuestionsFullResponse;
import com.example.examprepbackend.dto.response.questions.QuestionPublicResponse;
import com.example.examprepbackend.dto.response.teacher.DashboardStats;
import com.example.examprepbackend.dto.response.teacher.ScoreDistribution;
import com.example.examprepbackend.dto.response.users.StudentResponse;
import com.example.examprepbackend.entity.*;
import com.example.examprepbackend.exception.*;
import com.example.examprepbackend.repository.*;
import com.example.examprepbackend.service.ExamAttemptService;
import com.example.examprepbackend.service.QuestionService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ExamAttemptServiceImpl implements ExamAttemptService {

    private final ExamRepository examRepository;
    private final ExamAttemptRepository examAttemptRepository;
    private final UsersRepository usersRepository;
    private final QuestionRepository questionRepository;
    private final AnswerRepository answerRepository;
    private final UserAnswerRepository userAnswerRepository;
    private final ClassExamRepository classExamRepository;
    private final QuestionService questionService;
    private final ModelMapper modelMapper;


    private ExamStartResponse convertToStartDto(ExamAttempt examAttempt) {
        ExamStartResponse startResponse = new ExamStartResponse();

        startResponse.setAttemptId(examAttempt.getId());
        startResponse.setExamCode(examAttempt.getExam().getCode());
        startResponse.setExamTitle(examAttempt.getExam().getTitle());
        startResponse.setDuration(examAttempt.getExam().getDuration());
        startResponse.setExamType(examAttempt.getExam().getExamType().toString());
        startResponse.setStartTime(examAttempt.getStartTime());

        List<QuestionPublicResponse> questionPublicResponses = questionService.getQuestionsPublicByExamId(examAttempt.getExam().getId());
        startResponse.setQuestions(questionPublicResponses);

        return startResponse;

    }

    @Override
    public ExamStartResponse startExam(Integer examId, Authentication authentication) {

        //Kiem tra hoc sinh dang lam bai thi
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new ApplicationException("Unauthorized");
        }

        String username = authentication.getName();
        Optional<Users> usersOptional = usersRepository.findByUsername(username);
        if (usersOptional.isEmpty()) {
            throw new ApplicationException("User not found");
        }

        Users student = usersOptional.get();

        //Kiem tra de thi
        Optional<Exam> examOptional = examRepository.findById(examId);
        if (examOptional.isEmpty()) {
            throw new ApplicationException("Exam not found");
        }

        Exam exam = examOptional.get();

        //Kiem tra de thi co o trong lop cua hoc sinh khong
        ClassExam classExam = classExamRepository.findByClassIdAndExamId(student.getClasses().getId(), examId);
        if (classExam == null) {
            throw new ApplicationException("The selected exam does not belong to this class.");
        }

        //Kiem tra xem duoi database da co attempt voi examId va dang trong trang thai IN_PROGRESS
        ExamAttempt examAttemptExits = examAttemptRepository.findByExamAndStudentAndStatus(exam, student, AttemptStatus.IN_PROGRESS);
        if (examAttemptExits != null) {
            throw new ApplicationException("You already have an ongoing attempt for this exam. " +
                    "Please complete or submit it before starting a new one.");
        }

        //Tao exam_attempt va luu xuong database
        ExamAttempt examAttempt = new ExamAttempt();
        examAttempt.setExam(exam);
        examAttempt.setStudent(student);
        examAttempt.setStartTime(LocalDateTime.now());
        examAttempt.setScore(0.0);
        examAttempt.setCorrectCount(0);
        examAttempt.setWrongCount(0);
        examAttempt.setBlankCount(0);
        examAttempt.setTimeSpentSeconds(0);
        examAttempt.setStatus(AttemptStatus.IN_PROGRESS);

        try {
            examAttemptRepository.save(examAttempt);
            return convertToStartDto(examAttempt);
        } catch (RuntimeException exception) {
            throw new ApplicationException("Already has attempt");
        }
    }

    //Restart exam
    @Transactional
    @Override
    public ExamStartResponse restartExam(Integer examId, Authentication authentication) {
        //Kiem tra hoc sinh dang lam bai thi
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new ApplicationException("Unauthorized");
        }

        String username = authentication.getName();
        Optional<Users> usersOptional = usersRepository.findByUsername(username);
        if (usersOptional.isEmpty()) {
            throw new ApplicationException("User not found");
        }

        Users student = usersOptional.get();

        //Kiem tra de thi
        Optional<Exam> examOptional = examRepository.findById(examId);
        if (examOptional.isEmpty()) {
            throw new ApplicationException("Exam not found");
        }

        Exam exam = examOptional.get();

        //Kiem tra de thi co o trong lop cua hoc sinh khong
        ClassExam classExam = classExamRepository.findByClassIdAndExamId(student.getClasses().getId(), examId);
        if (classExam == null) {
            throw new ApplicationException("The selected exam does not belong to this class.");
        }

        //Tim de thi hoc sinh dang lam va restart
        ExamAttempt examAttemptExits = examAttemptRepository.findByExamAndStudentAndStatus(exam, student, AttemptStatus.IN_PROGRESS);
        if (examAttemptExits != null) {
            examAttemptRepository.delete(examAttemptExits);
        }

        //Tao exam_attempt va luu xuong database
        ExamAttempt examAttempt = new ExamAttempt();
        examAttempt.setExam(exam);
        examAttempt.setStudent(student);
        examAttempt.setStartTime(LocalDateTime.now());
        examAttempt.setScore(0.0);
        examAttempt.setCorrectCount(0);
        examAttempt.setWrongCount(0);
        examAttempt.setBlankCount(0);
        examAttempt.setTimeSpentSeconds(0);
        examAttempt.setStatus(AttemptStatus.IN_PROGRESS);

        try {
            examAttemptRepository.save(examAttempt);
            return convertToStartDto(examAttempt);
        } catch (RuntimeException exception) {
            throw new ApplicationException("Already has attempt");
        }
    }

    // Hieu
    @Transactional
    @Override
    public StartExamAttemptResponse startAttempt(Integer examId) {
        Users currentUser = getCurrentUser();

        Exam exam = examRepository.findById(examId)
                .orElseThrow(() -> new ResourceNotFoundException("Exam not found with id: " + examId));

        validateExamCanBeStarted(exam);

        ExamAttempt attempt = new ExamAttempt();
        attempt.setStudent(currentUser);
        attempt.setExam(exam);
        attempt.setStatus(AttemptStatus.IN_PROGRESS);
        attempt.setStartTime(LocalDateTime.now());

        ExamAttempt savedAttempt = examAttemptRepository.save(attempt);

        return StartExamAttemptResponse.builder()
                .attemptId(savedAttempt.getId())
                .examId(exam.getId())
                .examTitle(exam.getTitle())
                .status(savedAttempt.getStatus().name())
                .startTime(savedAttempt.getStartTime())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public AttemptQuestionsFullResponse getAttemptQuestions(Integer attemptId) {
        Users currentUser = getCurrentUser();

        ExamAttempt attempt = examAttemptRepository.findByIdAndStudentUsername(attemptId, currentUser.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("Attempt not found with id: " + attemptId));

        validateAttemptCanLoadQuestions(attempt);

        Exam exam = attempt.getExam();
        List<Question> questions = questionRepository.findQuestionsByExamId(exam.getId());

        List<Integer> questionIds = questions.stream()
                .map(Question::getId)
                .toList();

        Map<Integer, List<Answer>> answersByQuestionId = getAnswersByQuestionId(questionIds);

        List<AttemptQuestionResponse> questionResponses = questions.stream()
                .map(question -> toAttemptQuestionResponse(
                        question,
                        answersByQuestionId.getOrDefault(question.getId(), Collections.emptyList())
                ))
                .toList();

        return AttemptQuestionsFullResponse.builder()
                .attemptId(attempt.getId())
                .examId(exam.getId())
                .examTitle(exam.getTitle())
                .status(attempt.getStatus())
                .startTime(attempt.getStartTime())
                .questions(questionResponses)
                .build();
    }

    @Override
    @Transactional
    public SubmitExamAttemptResponse submitAttempt(Integer attemptId, SubmitExamAttemptRequest request) {
        Users currentUser = getCurrentUser();

        ExamAttempt attempt = examAttemptRepository.findByIdAndStudentUsername(attemptId, currentUser.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("Attempt not found with id: " + attemptId));

        validateAttemptCanBeSubmitted(attempt);

        Exam exam = attempt.getExam();
        List<Question> questions = questionRepository.findQuestionsByExamId(exam.getId());

        if (questions.isEmpty()) {
            throw new BadRequestException("This exam has no questions");
        }

        List<Integer> questionIds = questions.stream()
                .map(Question::getId)
                .toList();

        List<Answer> allAnswers = answerRepository.findByQuestionIdIn(questionIds);

        Map<Integer, Answer> answerMap = allAnswers.stream()
                .collect(Collectors.toMap(Answer::getId, Function.identity()));

        Map<Integer, SubmitAnswerRequest> submittedAnswerMap = normalizeSubmittedAnswers(request);

        ResultSummary result = gradeAndSaveAnswers(attempt, questions, answerMap, submittedAnswerMap);

        LocalDateTime endTime = LocalDateTime.now();
        int totalQuestions = questions.size();
        int timeSpentSeconds = calculateTimeSpentSeconds(attempt.getStartTime(), endTime);
        double score = calculateScore(result.correctCount(), totalQuestions);

        Double passScore = exam.getPassScore();
        boolean passed = score >= passScore;
        String resultStatus = passed ? "PASSED" : "FAILED";

        attempt.setEndTime(endTime);
        attempt.setStatus(AttemptStatus.SUBMITTED);
        attempt.setScore(score);
        attempt.setCorrectCount(result.correctCount());
        attempt.setWrongCount(result.wrongCount());
        attempt.setBlankCount(result.blankCount());
        attempt.setTimeSpentSeconds(timeSpentSeconds);

        examAttemptRepository.save(attempt);

        return SubmitExamAttemptResponse.builder()
                .attemptId(attempt.getId())
                .examId(exam.getId())
                .examTitle(exam.getTitle())
                .examType(exam.getExamType())
                .score(score)
                .passScore(passScore)
                .passed(passed)
                .resultStatus(resultStatus)
                .totalQuestions(totalQuestions)
                .correctCount(result.correctCount())
                .wrongCount(result.wrongCount())
                .blankCount(result.blankCount())
                .timeSpentSeconds(timeSpentSeconds)
                .reviewAllowed(exam.getReviewAllowed())
                .endTime(endTime)
                .message("Submit exam successfully")
                .build();
    }

    private Users getCurrentUser() {
        String currentUsername = getValidatedCurrentUsername();

        return usersRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with username: " + currentUsername));
    }

    private String getValidatedCurrentUsername() {
        String currentUsername = SecurityUtils.getCurrentUsername();
        if (currentUsername == null || currentUsername.isBlank()) {
            throw new BadRequestException("Current user is not authenticated");
        }
        return currentUsername;
    }

    private void validateExamCanBeStarted(Exam exam) {
        if (exam.getIsActive() == null || !exam.getIsActive()) {
            throw new BadRequestException("Exam is not active");
        }
    }

    private void validateAttemptCanLoadQuestions(ExamAttempt attempt) {
        if (attempt.getStatus() != AttemptStatus.IN_PROGRESS) {
            throw new BadRequestException("Cannot load questions because this attempt is no longer in progress");
        }
    }

    private void validateAttemptCanBeSubmitted(ExamAttempt attempt) {
        if (attempt.getStatus() == AttemptStatus.SUBMITTED) {
            throw new ConflictException("This attempt has already been submitted");
        }

        if (attempt.getStatus() != AttemptStatus.IN_PROGRESS) {
            throw new BadRequestException("This attempt is not in a submittable state");
        }
    }

    private Map<Integer, List<Answer>> getAnswersByQuestionId(List<Integer> questionIds) {
        if (questionIds == null || questionIds.isEmpty()) {
            return Collections.emptyMap();
        }

        return answerRepository.findByQuestionIdIn(questionIds)
                .stream()
                .collect(Collectors.groupingBy(answer -> answer.getQuestion().getId()));
    }

    private AttemptQuestionResponse toAttemptQuestionResponse(Question question, List<Answer> answers) {
        List<AttemptQuestionOptionResponse> optionResponses = answers.stream()
                .sorted(Comparator.comparing(answer -> {
                    String label = answer.getLabel();
                    return label == null ? "" : label;
                }))
                .map(answer -> AttemptQuestionOptionResponse.builder()
                        .optionId(answer.getId())
                        .optionLabel(answer.getLabel())
                        .optionContent(answer.getContent())
                        .build())
                .toList();

        return AttemptQuestionResponse.builder()
                .questionId(question.getId())
//                .questionOrder(question.getOrderNo())
                .questionContent(question.getContent())
                .options(optionResponses)
                .build();
    }

    private Map<Integer, SubmitAnswerRequest> normalizeSubmittedAnswers(SubmitExamAttemptRequest request) {
        if (request == null || request.getAnswers() == null || request.getAnswers().isEmpty()) {
            return Collections.emptyMap();
        }

        Map<Integer, SubmitAnswerRequest> result = new HashMap<>();

        for (SubmitAnswerRequest answer : request.getAnswers()) {
            if (answer == null || answer.getQuestionId() == null) {
                throw new BadRequestException("Question id is required in submitted answers");
            }

            if (result.containsKey(answer.getQuestionId())) {
                throw new BadRequestException("Duplicate answer detected for question id: " + answer.getQuestionId());
            }

            result.put(answer.getQuestionId(), answer);
        }

        return result;
    }

    private ResultSummary gradeAndSaveAnswers(
            ExamAttempt attempt,
            List<Question> questions,
            Map<Integer, Answer> answerMap,
            Map<Integer, SubmitAnswerRequest> submittedAnswerMap
    ) {
        int correctCount = 0;
        int wrongCount = 0;
        int blankCount = 0;

        List<StudentAnswer> studentAnswersToSave = new ArrayList<>();

        for (Question question : questions) {
            SubmitAnswerRequest submitted = submittedAnswerMap.get(question.getId());

            if (submitted == null || submitted.getSelectedOptionId() == null) {
                blankCount++;
                studentAnswersToSave.add(buildUserAnswer(attempt, question, null, false));
                continue;
            }

            Answer selectedAnswer = answerMap.get(submitted.getSelectedOptionId());
            validateSelectedAnswerBelongsToQuestion(selectedAnswer, question, submitted.getSelectedOptionId());

            boolean isCorrect = Boolean.TRUE.equals(selectedAnswer.getIsCorrect());

            if (isCorrect) {
                correctCount++;
            } else {
                wrongCount++;
            }

            studentAnswersToSave.add(buildUserAnswer(attempt, question, selectedAnswer, isCorrect));
        }

        userAnswerRepository.saveAll(studentAnswersToSave);
        return new ResultSummary(correctCount, wrongCount, blankCount);
    }

    private StudentAnswer buildUserAnswer(ExamAttempt attempt, Question question, Answer selectedAnswer, boolean isCorrect) {
        StudentAnswer studentAnswer = new StudentAnswer();
        studentAnswer.setAttempt(attempt);
        studentAnswer.setQuestion(question);
        studentAnswer.setSelectedAnswer(selectedAnswer);
        studentAnswer.setIsCorrect(isCorrect);
        return studentAnswer;
    }

    private void validateSelectedAnswerBelongsToQuestion(Answer selectedAnswer, Question question, Integer selectedAnswerId) {
        if (selectedAnswer == null) {
            throw new BadRequestException("Selected answer not found with id: " + selectedAnswerId);
        }

        if (!Objects.equals(selectedAnswer.getQuestion().getId(), question.getId())) {
            throw new BadRequestException(
                    "Selected answer id " + selectedAnswerId + " does not belong to question id " + question.getId()
            );
        }
    }

    private double calculateScore(int correctCount, int totalQuestions) {
        if (totalQuestions <= 0) {
            return 0.0;
        }
        // tính điểm
        return (correctCount * 100.0) / totalQuestions;
    }

    private int calculateTimeSpentSeconds(LocalDateTime startTime, LocalDateTime submittedAt) {
        if (startTime == null || submittedAt == null) {
            return 0;
        }

        long seconds = Duration.between(startTime, submittedAt).getSeconds();
        return (int) Math.max(seconds, 0);
    }

    private record ResultSummary(int correctCount, int wrongCount, int blankCount) {
    }


    @Transactional(readOnly = true)
    @Override
    public AttemptResultResponse getAttemptResult(Integer attemptId) {
        Users currentUser = getCurrentUser();

        ExamAttempt attempt = examAttemptRepository.findByIdAndStudentUsername(attemptId, currentUser.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("Attempt not found with id: " + attemptId));

        validateAttemptCanViewResult(attempt);

        Exam exam = attempt.getExam();

        int correctCount = defaultIfNull(attempt.getCorrectCount());
        int wrongCount = defaultIfNull(attempt.getWrongCount());
        int blankCount = defaultIfNull(attempt.getBlankCount());

        int totalQuestions = correctCount + wrongCount + blankCount;

        if (totalQuestions == 0 && exam != null) {
            totalQuestions = questionRepository.findQuestionsByExamId(exam.getId()).size();
        }

        double score = attempt.getScore() != null ? attempt.getScore() : 0.0;
        double passScore = exam.getPassScore() != null ? exam.getPassScore() : 0.0;
        boolean passed = score >= passScore;
        String resultStatus = passed ? "PASSED" : "FAILED";

        return AttemptResultResponse.builder()
                .attemptId(attempt.getId())
                .examId(exam.getId())
                .examTitle(exam.getTitle())
                .examType(exam.getExamType())
                .score(score)
                .passScore(passScore)
                .passed(passed)
                .resultStatus(resultStatus)
                .totalQuestions(totalQuestions)
                .correctCount(correctCount)
                .wrongCount(wrongCount)
                .blankCount(blankCount)
                .timeSpentSeconds(defaultIfNull(attempt.getTimeSpentSeconds()))
                .reviewAllowed(isReviewAllowed(exam))
                .submittedAt(attempt.getEndTime())
                .build();
    }

    private void validateAttemptCanViewResult(ExamAttempt attempt) {
        if (attempt.getStatus() != AttemptStatus.SUBMITTED
                && attempt.getStatus() != AttemptStatus.GRADED) {
            throw new BadRequestException("Cannot view result because this attempt has not been submitted yet");
        }
    }

    private void validateAttemptOwner(ExamAttempt attempt, Users currentUser) {
        if (!Objects.equals(attempt.getStudent().getId(), currentUser.getId())) {
            throw new ForbiddenException("Access denied");
        }
    }

    private int defaultIfNull(Integer value) {
        return value == null ? 0 : value;
    }


    private static final Set<ExamType> REVIEW_ALLOWED_TYPES =
            Set.of(ExamType.MOCK, ExamType.PRACTICE);

    private boolean isReviewAllowed(Exam exam) {
        return exam != null
                && exam.getExamType() != null
                && REVIEW_ALLOWED_TYPES.contains(exam.getExamType());
    }


    @Override
    @Transactional(readOnly = true)
    public AttemptReviewDetailResponse getAttemptReviewDetail(Integer attemptId) {
        Users currentUser = getCurrentUser();

        ExamAttempt attempt = examAttemptRepository.findById(attemptId)
                .orElseThrow(() -> new ResourceNotFoundException("Attempt not found with id: " + attemptId));

        validateAttemptOwner(attempt, currentUser);
        validateAttemptCanViewResult(attempt);

        Exam exam = attempt.getExam();
        if (exam == null) {
            throw new ResourceNotFoundException("Exam not found for attempt id: " + attemptId);
        }

        validateReviewAllowed(exam);

        List<Question> questions = questionRepository.findQuestionsByExamId(exam.getId());

        List<Integer> questionIds = questions.stream()
                .map(Question::getId)
                .toList();

        List<Answer> allAnswers = questionIds.isEmpty()
                ? Collections.emptyList()
                : answerRepository.findByQuestionIdIn(questionIds);

        Map<Integer, List<Answer>> answersByQuestionId = allAnswers.stream()
                .collect(Collectors.groupingBy(answer -> answer.getQuestion().getId()));

        Map<Integer, Answer> correctAnswerByQuestionId = allAnswers.stream()
                .filter(answer -> Boolean.TRUE.equals(answer.getIsCorrect()))
                .collect(Collectors.toMap(
                        answer -> answer.getQuestion().getId(),
                        Function.identity(),
                        (existing, replacement) -> existing
                ));

        List<StudentAnswer> studentAnswers = userAnswerRepository.findByAttemptId(attemptId);

        Map<Integer, StudentAnswer> studentAnswerByQuestionId = studentAnswers.stream()
                .collect(Collectors.toMap(
                        studentAnswer -> studentAnswer.getQuestion().getId(),
                        Function.identity(),
                        (existing, replacement) -> existing
                ));

        List<ReviewQuestionResponse> questionResponses = questions.stream()
                .map(question -> mapToReviewQuestionResponse(
                        question,
                        answersByQuestionId.getOrDefault(question.getId(), Collections.emptyList()),
                        correctAnswerByQuestionId.get(question.getId()),
                        studentAnswerByQuestionId.get(question.getId())
                ))
                .toList();

        return AttemptReviewDetailResponse.builder()
                .attemptId(attempt.getId())
                .examId(exam.getId())
                .examTitle(exam.getTitle())
                .examType(exam.getExamType())
                .reviewAllowed(true)
                .submittedAt(attempt.getEndTime())
                .questions(questionResponses)
                .build();
    }

    private ReviewQuestionResponse mapToReviewQuestionResponse(
            Question question,
            List<Answer> answers,
            Answer correctAnswer,
            StudentAnswer studentAnswer
    ) {
        Integer selectedOptionId = null;
        if (studentAnswer != null && studentAnswer.getSelectedAnswer() != null) {
            selectedOptionId = studentAnswer.getSelectedAnswer().getId();
        }

        Integer correctOptionId = correctAnswer != null ? correctAnswer.getId() : null;

        List<ReviewOptionResponse> optionResponses = answers.stream()
                .sorted(Comparator.comparing(answer -> {
                    String label = answer.getLabel();
                    return label == null ? "" : label;
                }))
                .map(answer -> ReviewOptionResponse.builder()
                        .optionId(answer.getId())
                        .optionLabel(answer.getLabel())
                        .optionContent(answer.getContent())
                        .isCorrect(Boolean.TRUE.equals(answer.getIsCorrect()))
                        .build())
                .toList();

        return ReviewQuestionResponse.builder()
                .questionId(question.getId())
//                .questionOrder(question.getOrderNo())
                .questionContent(question.getContent())
                .selectedOptionId(selectedOptionId)
                .correctOptionId(correctOptionId)
                .isCorrect(studentAnswer != null && Boolean.TRUE.equals(studentAnswer.getIsCorrect()))
                .explanation(question.getExplanation())
                .options(optionResponses)
                .build();
    }


    private void validateReviewAllowed(Exam exam) {
        if (!isReviewAllowed(exam)) {
            throw new BadRequestException("Review detail is not allowed for this exam");
        }
    }


    //Lay danh sach ket qua de thi
    @Override
    public Page<ExamAttemptResponse> getAttemptsByExamType(Authentication authentication, Pageable pageable, String examType) {
        if (examType == null || examType.isBlank()) {
            throw new ApplicationException("exam type not null or not blank");
        }

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new ApplicationException("Unauthorized");
        }

        String username = authentication.getName();
        Users user = usersRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        Page<ExamAttempt> attempts;

        attempts = examAttemptRepository.findByStudentAndExamExamType(user, ExamType.valueOf(examType), pageable);

        return attempts.map(attempt -> {
            ExamAttemptResponse res = new ExamAttemptResponse();
            res.setId(attempt.getId());
            res.setExam(modelMapper.map(attempt.getExam(), ExamSummaryResponse.class));
            res.setStudent(modelMapper.map(attempt.getStudent(), StudentResponse.class));
            res.setStartTime(attempt.getStartTime());
            res.setEndTime(attempt.getEndTime());
            res.setScore(attempt.getScore());
            res.setStatus(attempt.getStatus());
            res.setBlankCount(attempt.getBlankCount());
            res.setCorrectCount(attempt.getCorrectCount());
            res.setWrongCount(attempt.getWrongCount());
            res.setTimeSpentSeconds(attempt.getTimeSpentSeconds());
            return res;
        });
    }

    // hải
    @Override
    public List<ScoreDistribution> getScoreDistribution() {
//        return examAttemptRepository.getScoreDistribution();
        // fix nếu số điểm k nằm trong cột nào
        List<ScoreDistribution> data = examAttemptRepository.getScoreDistribution();

        Map<String, Long> map = new HashMap<>();
        for (ScoreDistribution d : data) {
            map.put(d.getRange(), d.getCount());
        }
        List<String> ranges = List.of(
                "0-4", "4-5", "5-6", "6-7", "7-8", "8-9", "9-10"
        );
        List<ScoreDistribution> result = new ArrayList<>();
        for (String r : ranges) {
            result.add(new ScoreDistribution(r, map.getOrDefault(r, 0L)));
        }
        return result;
    }

    @Override
    public DashboardStats getStats() {
        String username = SecurityUtils.getCurrentUsername();

        Users user = usersRepository.findByUsername(username).orElse(null);
        if (user == null) {
            throw new RuntimeException("User not found");
        }

        long totalExams = examRepository.countByCreator_Username(username);
        long totalQuestions = questionRepository.countByCreator_Username(username);
        long totalStudents = usersRepository.countStudentsByTeacher(user.getId());

        return new DashboardStats(
                totalExams,
                totalQuestions,
                totalStudents
        );
    }
}














