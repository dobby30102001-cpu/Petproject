
DROP DATABASE IF EXISTS exam_management_system;
CREATE DATABASE exam_management_system;
USE exam_management_system;

-- ================= CLASSES =================
CREATE TABLE classes
(
    id          INT PRIMARY KEY AUTO_INCREMENT,
    name        VARCHAR(255) NOT NULL UNIQUE,
    create_date DATETIME
);

-- ================= USERS =================
CREATE TABLE users
(
    id          INT PRIMARY KEY AUTO_INCREMENT,
    email       VARCHAR(255)                       NOT NULL UNIQUE,
    username    VARCHAR(255)                       NOT NULL UNIQUE,
    password    VARCHAR(255)                       NOT NULL,
    first_name  VARCHAR(255)                       NOT NULL,
    last_name   VARCHAR(255)                       NOT NULL,
    role        ENUM ('ADMIN','TEACHER','STUDENT') NOT NULL,
    is_active   BOOLEAN                            NOT NULL DEFAULT TRUE,
    status      ENUM ('ACTIVED','LOCKED')          NOT NULL DEFAULT 'ACTIVED',
    class_id    INT,
    create_date DATETIME,
    fail_count  INT                                         DEFAULT 0,
    lock_time   DATETIME,
    FOREIGN KEY (class_id) REFERENCES classes (id)
);

-- ================= CLASS TEACHER =================
CREATE TABLE class_teacher
(
    class_id   INT NOT NULL,
    teacher_id INT NOT NULL,

    PRIMARY KEY (class_id, teacher_id),

    FOREIGN KEY (class_id) REFERENCES classes (id),
    FOREIGN KEY (teacher_id) REFERENCES users (id)
);

-- ================= CATEGORY QUESTION =================
CREATE TABLE category_question
(
    id   INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL UNIQUE
);

-- ================= QUESTION =================
CREATE TABLE question
(
    id               INT PRIMARY KEY AUTO_INCREMENT,
    content          TEXT                          NOT NULL,
    difficulty_level ENUM ('EASY','MEDIUM','HARD') NOT NULL,
    category_id      INT                           NOT NULL,
    creator_id       INT                           NOT NULL,
    create_date      DATETIME,
    explanation      TEXT,

    FOREIGN KEY (category_id) REFERENCES category_question (id),
    FOREIGN KEY (creator_id) REFERENCES users (id)
);

-- ================= ANSWER =================
CREATE TABLE answer
(
    id          INT PRIMARY KEY AUTO_INCREMENT,
    content     TEXT         NOT NULL,
    question_id INT          NOT NULL,
    is_correct  BOOLEAN      NOT NULL,
    label       VARCHAR(255) NULL,

    FOREIGN KEY (question_id) REFERENCES question (id) ON DELETE CASCADE
);

-- ================= EXAM =================
CREATE TABLE exam
(
    id             INT PRIMARY KEY AUTO_INCREMENT,
    code           VARCHAR(255)                          NOT NULL UNIQUE,
    title          VARCHAR(255)                          NOT NULL,
    duration       TIME                                  NOT NULL,
    category_id    INT                                   NOT NULL,
    creator_id     INT                                   NOT NULL,
    create_date    DATETIME,
    is_active      TINYINT(1)                            NOT NULL DEFAULT 1,
    exam_type      ENUM ('PRACTICE', 'OFFICIAL', 'MOCK') NOT NULL,
    pass_score     DOUBLE                                NOT NULL DEFAULT 50.0,
    review_allowed TINYINT(1)                            NOT NULL DEFAULT 1,

    FOREIGN KEY (category_id) REFERENCES category_question (id),
    FOREIGN KEY (creator_id) REFERENCES users (id)
);

-- ================= EXAM QUESTION =================
CREATE TABLE exam_question
(
    exam_id     INT NOT NULL,
    question_id INT NOT NULL,

    PRIMARY KEY (exam_id, question_id),

    FOREIGN KEY (exam_id) REFERENCES exam (id) ON DELETE CASCADE,
    FOREIGN KEY (question_id) REFERENCES question (id) ON DELETE CASCADE
);

-- ================= FAVORITE EXAM =================
CREATE TABLE favorite_exam
(
    exam_id    INT NOT NULL,
    student_id INT NOT NULL,

    PRIMARY KEY (exam_id, student_id),

    FOREIGN KEY (exam_id) REFERENCES exam (id) ON DELETE CASCADE,
    FOREIGN KEY (student_id) REFERENCES users (id) ON DELETE CASCADE
);

-- ================= EXAM ATTEMPT =================
CREATE TABLE exam_attempt
(
    id                 INT PRIMARY KEY AUTO_INCREMENT,
    exam_id            INT                              NOT NULL,
    student_id         INT                              NOT NULL,
    start_time         DATETIME,
    end_time           DATETIME,
    score              DECIMAL(4, 2),
    correct_count      INT                              NOT NULL DEFAULT 0,
    wrong_count        INT                              NOT NULL DEFAULT 0,
    blank_count        INT                              NOT NULL DEFAULT 0,
    time_spent_seconds INT                              NOT NULL DEFAULT 0,
    status             ENUM ('IN_PROGRESS','SUBMITTED') NOT NULL,

    FOREIGN KEY (exam_id) REFERENCES exam (id) ON DELETE CASCADE,
    FOREIGN KEY (student_id) REFERENCES users (id) ON DELETE CASCADE
);

-- ================= STUDENT ANSWER =================
CREATE TABLE student_answer
(
    id                 INT PRIMARY KEY AUTO_INCREMENT,
    attempt_id         INT     NOT NULL,
    question_id        INT     NOT NULL,
    selected_answer_id INT     NULL,
    is_correct         BOOLEAN NOT NULL DEFAULT FALSE,

    FOREIGN KEY (attempt_id) REFERENCES exam_attempt (id) ON DELETE CASCADE,
    FOREIGN KEY (question_id) REFERENCES question (id) ON DELETE CASCADE,
    FOREIGN KEY (selected_answer_id) REFERENCES answer (id) ON DELETE CASCADE
);
CREATE TABLE class_exam
(
    id            INT PRIMARY KEY AUTO_INCREMENT,
    class_id      INT NOT NULL,
    exam_id       INT NOT NULL,
    attempt_count INT,

    -- tranh trung class voi xam
    CONSTRAINT unique_class_exam UNIQUE (class_id, exam_id),
    FOREIGN KEY (class_id) REFERENCES classes (id) ON DELETE CASCADE,
    FOREIGN KEY (exam_id) REFERENCES exam (id) ON DELETE CASCADE
);
-- ================= OTP TABLE =================
CREATE TABLE otps
(
    id         INT AUTO_INCREMENT PRIMARY KEY,
    email      VARCHAR(255) NOT NULL,
    otp        INT          NOT NULL,
    type       VARCHAR(50),
    expire_at  DATETIME,
    created_at DATETIME,
    FOREIGN KEY (email) REFERENCES users (email)
);
INSERT INTO classes (name, create_date)
VALUES ('Railway01', now()),
       ('Railway02', now()),
       ('Railway03', now()),
       ('Rocket01', now()),
       ('Rocket02', now());

INSERT INTO users
(email, username, password, first_name, last_name, role, is_active, status, class_id, create_date)
VALUES ('admin1@mail.com', 'admin1', 'admin123', 'Hai', 'Dong', 'ADMIN', true, 'ACTIVED', NULL, now()),
       ('admin2@mail.com', 'admin2', 'admin123', 'Tai', 'Pham', 'ADMIN', true, 'ACTIVED', NULL, now()),
       ('teacher1@mail.com', 'teacher1', '12345', 'Binh', 'Tran', 'TEACHER', true, 'ACTIVED', NULL, now()),
       ('teacher2@mail.com', 'teacher2', '12345', 'Cuong', 'Le', 'TEACHER', true, 'ACTIVED', NULL, now()),
       ('student1@mail.com', 'student1', '1234', 'Dung', 'Pham', 'STUDENT', true, 'ACTIVED', 1, now()),
       ('student2@mail.com', 'student2', '1234', 'Huy', 'Hoang', 'STUDENT', true, 'ACTIVED', 2, now());

INSERT INTO class_teacher (class_id, teacher_id)
VALUES (1, 3),
       (1, 4),
       (2, 3),
       (3, 4),
       (4, 3),
       (5, 4);
INSERT INTO category_question (name)
VALUES ('Java'),
       ('Spring'),
       ('SQL'),
       ('HTML'),
       ('JavaScript'),
       ('CSS');
INSERT INTO question
(content, difficulty_level, category_id, creator_id, create_date, explanation)
VALUES ('What is Java?', 'EASY', 1, 2, '2024-02-01',
        'Java is a high-level programming language used to build applications'),

       ('Explain OOP principles', 'MEDIUM', 1, 2, '2024-02-02',
        'OOP has four main principles: Encapsulation, Inheritance, Polymorphism, and Abstraction'),

       ('What is Spring Boot?', 'EASY', 2, 3, '2024-02-03',
        'Spring Boot is a framework that simplifies the development of Spring applications'),

       ('What is Primary Key?', 'EASY', 3, 2, '2024-02-04',
        'A primary key uniquely identifies each record in a database table'),

       ('What is HTML?', 'EASY', 4, 3, '2024-02-05',
        'HTML is a markup language used to structure web pages'),

       ('What is CSS ?', 'EASY', 6, 1, '2024-02-05',
        'CSS is a stylesheet language used for designing web pages');

INSERT INTO answer (content, question_id, is_correct)
VALUES
-- Question 1
('Programming Language', 1, true),
('Database', 1, false),
('Operating System', 1, false),
('Web Browser', 1, false),

-- Question 2
('Encapsulation', 2, true),
('Inheritance', 2, false),
('Compilation', 2, false),
('Indexing', 2, false),

-- Question 3
('Java Framework', 3, false),
('Spring Boot Framework', 3, true),
('Database Tool', 3, false),
('Programming Language', 3, false),

-- Question 4
('Primary key is unique identifier', 4, true),
('Primary key allows duplicate', 4, false),
('Primary key can be null', 4, false),
('Primary key is optional', 4, false),

-- Question 5
('Programming Language', 5, false),
('Markup Language', 5, true),
('Database System', 5, false),
('Operating System', 5, false),
-- Question 6
('Programming Language', 6, false),
('Markup Language', 6, true),
('Database System', 6, false),
('Operating System', 6, false);
INSERT INTO exam
    (code, title, duration, category_id, creator_id, create_date)
VALUES ('EX001', 'Java Basic Test', '00:30:00', 1, 3, NOW()),
       ('EX002', 'Spring Test', '00:40:00', 2, 3, NOW()),
       ('EX003', 'SQL Test', '00:30:00', 3, 2, NOW()),
       ('EX004', 'HTML Test', '00:20:00', 4, 3, NOW()),
       ('EX005', 'JS Test', '00:25:00', 5, 2, NOW()),
       ('EX006', 'JS 1', '00:25:00', 5, 2, NOW());
INSERT INTO exam_question (exam_id, question_id)
VALUES (1, 1),
       (1, 2),
       (2, 3),
       (3, 4),
       (4, 5),
       (5, 1),
       (6, 2);

INSERT INTO favorite_exam (exam_id, student_id)
VALUES (1, 6),
       (2, 5),
       (3, 6),
       (4, 5),
       (5, 6),
       (6, 5);

INSERT INTO exam_attempt
    (exam_id, student_id, start_time, end_time, score, status)
VALUES (1, 5, '2024-04-01 09:00:00', '2024-04-01 09:25:00', 8, 'SUBMITTED'),
       (2, 6, '2024-04-01 10:00:00', '2024-04-01 10:35:00', 7, 'SUBMITTED'),
       (3, 5, '2024-04-02 09:00:00', '2024-04-02 09:30:00', 6, 'SUBMITTED'),
       (4, 6, '2024-04-02 10:00:00', '2024-04-02 10:20:00', 9, 'SUBMITTED'),
       (5, 5, '2024-04-03 09:00:00', '2024-04-03 09:25:00', 10, 'SUBMITTED'),
       (6, 6, '2024-04-03 10:00:00', '2024-04-03 10:25:00', 8, 'SUBMITTED');


INSERT INTO student_answer (attempt_id, question_id, selected_answer_id, is_correct)
VALUES (1, 1, 1, TRUE),
       (2, 2, 6, FALSE),
       (3, 3, 10, TRUE),
       (4, 4, 14, FALSE),
       (5, 5, 18, TRUE),
       (6, 6, 22, FALSE);

INSERT INTO class_exam (class_id, exam_id)
VALUES (1, 1),
       (2, 2),
       (3, 3),
       (4, 4),
       (5, 5),
       (5, 6);

update users
set password ='$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne'
where id = 1; -- ADMIN:1234
update users
set password ='$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne'
where id = 2; -- ADMIN:1234
update users
set password ='$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne'
where id = 3; -- TEACHER:1234
update users
set password ='$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne'
where id = 4; -- TEACHER:1234
update users
set password ='$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne'
where id = 5; -- STUDENT:1234
update users
set password ='$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne'
where id = 6; -- STUDENT:1234
update users
set email ='ngoquangtruongjk05@gmail.com'
where id = 1;

