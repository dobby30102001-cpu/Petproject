package com.example.examprepbackend.service.impl;

import com.example.examprepbackend.constant.AttemptStatus;
import com.example.examprepbackend.constant.ExamType;
import com.example.examprepbackend.dto.request.exams.SubmitAnswerRequest;
import com.example.examprepbackend.dto.request.exams.SubmitExamAttemptRequest;
import com.example.examprepbackend.dto.response.exams.SubmitExamAttemptResponse;
import com.example.examprepbackend.entity.Answer;
import com.example.examprepbackend.entity.Exam;
import com.example.examprepbackend.entity.ExamAttempt;
import com.example.examprepbackend.entity.Question;
import com.example.examprepbackend.entity.Users;
import com.example.examprepbackend.exception.BadRequestException;
import com.example.examprepbackend.exception.ConflictException;
import com.example.examprepbackend.repository.AnswerRepository;
import com.example.examprepbackend.repository.ExamAttemptRepository;
import com.example.examprepbackend.repository.QuestionRepository;
import com.example.examprepbackend.repository.UsersRepository;
import com.example.examprepbackend.repository.UserAnswerRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ExamAttemptServiceImplTest {

    @Mock private ExamAttemptRepository examAttemptRepository;
    @Mock private QuestionRepository questionRepository;
    @Mock private AnswerRepository answerRepository;
    @Mock private UserAnswerRepository userAnswerRepository;
    @Mock private UsersRepository usersRepository;

    @InjectMocks
    private ExamAttemptServiceImpl service;

    private Users student;
    private Exam exam;

    @BeforeEach
    void setUp() {
        student = new Users();
        student.setId(1);
        student.setUsername("student1");

        exam = new Exam();
        exam.setId(100);
        exam.setTitle("Sample Exam");
        exam.setExamType(ExamType.MOCK);
        exam.setPassScore(50.0);
        exam.setReviewAllowed(true);
        exam.setDuration(LocalTime.of(0, 30));   // 30 minutes

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(
                        "student1",
                        null,
                        List.of(new SimpleGrantedAuthority("ROLE_STUDENT"))
                )
        );
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    private ExamAttempt inProgressAttempt(LocalDateTime startedAt) {
        ExamAttempt attempt = new ExamAttempt();
        attempt.setId(500);
        attempt.setExam(exam);
        attempt.setStudent(student);
        attempt.setStartTime(startedAt);
        attempt.setStatus(AttemptStatus.IN_PROGRESS);
        return attempt;
    }

    private Question question(int id, String content) {
        Question q = new Question();
        q.setId(id);
        q.setContent(content);
        return q;
    }

    private Answer answer(int id, Question q, boolean correct) {
        Answer a = new Answer();
        a.setId(id);
        a.setQuestion(q);
        a.setIsCorrect(correct);
        return a;
    }

    private SubmitExamAttemptRequest submissionOf(int questionId, int optionId) {
        SubmitAnswerRequest sa = new SubmitAnswerRequest();
        sa.setQuestionId(questionId);
        sa.setSelectedOptionId(optionId);
        SubmitExamAttemptRequest req = new SubmitExamAttemptRequest();
        req.setAnswers(List.of(sa));
        return req;
    }

    @Test
    void submitAttempt_scoresCorrectlyWithMixOfRightWrongAndBlank() {
        // 3 questions total. Student answers Q1 correct, Q2 wrong, leaves Q3 blank.
        // Expected score = 1/3 * 100 = 33.333...
        ExamAttempt attempt = inProgressAttempt(LocalDateTime.now().minusMinutes(5));
        Question q1 = question(1, "Q1");
        Question q2 = question(2, "Q2");
        Question q3 = question(3, "Q3");
        Answer a1Right = answer(11, q1, true);
        Answer a2Wrong = answer(22, q2, false);
        Answer a3Right = answer(33, q3, true);   // stays unpicked → Q3 counts as blank

        when(usersRepository.findByUsername("student1")).thenReturn(Optional.of(student));
        when(examAttemptRepository.findByIdAndStudentUsername(500, "student1"))
                .thenReturn(Optional.of(attempt));
        when(questionRepository.findQuestionsByExamId(100)).thenReturn(List.of(q1, q2, q3));
        when(answerRepository.findByQuestionIdIn(List.of(1, 2, 3)))
                .thenReturn(List.of(a1Right, a2Wrong, a3Right));
        when(examAttemptRepository.save(any(ExamAttempt.class))).thenAnswer(inv -> inv.getArgument(0));

        SubmitExamAttemptRequest request = new SubmitExamAttemptRequest();
        SubmitAnswerRequest pickQ1 = new SubmitAnswerRequest();
        pickQ1.setQuestionId(1);
        pickQ1.setSelectedOptionId(11);
        SubmitAnswerRequest pickQ2 = new SubmitAnswerRequest();
        pickQ2.setQuestionId(2);
        pickQ2.setSelectedOptionId(22);
        request.setAnswers(List.of(pickQ1, pickQ2));

        SubmitExamAttemptResponse response = service.submitAttempt(500, request);

        assertEquals(3, response.getTotalQuestions());
        assertEquals(1, response.getCorrectCount());
        assertEquals(1, response.getWrongCount());
        assertEquals(1, response.getBlankCount());
        assertEquals(100.0 / 3.0, response.getScore(), 1e-6);
        assertFalse(response.getPassed(), "1/3 should be below the 50% pass line");
        assertEquals("FAILED", response.getResultStatus());
        assertEquals(AttemptStatus.SUBMITTED, attempt.getStatus());
        verify(userAnswerRepository).saveAll(anyList());
    }

    @Test
    void submitAttempt_throwsWhenSubmittedAfterTimeExpiry() {
        // Exam duration 30m, but attempt started 2 hours ago → past the grace window.
        ExamAttempt expired = inProgressAttempt(LocalDateTime.now().minusHours(2));

        when(usersRepository.findByUsername("student1")).thenReturn(Optional.of(student));
        when(examAttemptRepository.findByIdAndStudentUsername(500, "student1"))
                .thenReturn(Optional.of(expired));

        BadRequestException ex = assertThrows(
                BadRequestException.class,
                () -> service.submitAttempt(500, submissionOf(1, 11))
        );
        assertTrue(ex.getMessage().toLowerCase().contains("expired"));

        // No grading / persistence should happen for an expired attempt.
        verify(examAttemptRepository, never()).save(any(ExamAttempt.class));
        verify(userAnswerRepository, never()).saveAll(anyList());
    }

    @Test
    void submitAttempt_throwsConflictWhenAlreadySubmitted() {
        ExamAttempt alreadySubmitted = inProgressAttempt(LocalDateTime.now().minusMinutes(10));
        alreadySubmitted.setStatus(AttemptStatus.SUBMITTED);
        alreadySubmitted.setEndTime(LocalDateTime.now().minusMinutes(5));

        when(usersRepository.findByUsername("student1")).thenReturn(Optional.of(student));
        when(examAttemptRepository.findByIdAndStudentUsername(500, "student1"))
                .thenReturn(Optional.of(alreadySubmitted));

        ConflictException ex = assertThrows(
                ConflictException.class,
                () -> service.submitAttempt(500, submissionOf(1, 11))
        );
        assertTrue(ex.getMessage().toLowerCase().contains("already"));

        verify(examAttemptRepository, never()).save(any(ExamAttempt.class));
        verify(userAnswerRepository, never()).saveAll(anyList());
    }
}
