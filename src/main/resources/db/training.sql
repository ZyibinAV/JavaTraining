
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY ,
    username VARCHAR(100) NOT NULL UNIQUE ,
    password_hash VARCHAR(255) NOT NULL ,
    email VARCHAR(100) NOT NULL  UNIQUE ,
    nickname VARCHAR(100) ,
    about TEXT ,
    avatar_path VARCHAR(255) ,
    role VARCHAR(20) NOT NULL ,
    created_at TIMESTAMP NOT NULL ,
    blocked BOOLEAN NOT NULL DEFAULT FALSE
);

CREATE TABLE topics (
    id BIGSERIAL PRIMARY KEY ,
    code VARCHAR(50) NOT NULL UNIQUE ,
    display_name VARCHAR(100) NOT NULL
);

CREATE TABLE questions (
    id BIGSERIAL PRIMARY KEY ,
    topic_id BIGINT NOT NULL ,
    question_text TEXT NOT NULL ,
    correct_answer_index INT NOT NULL ,
    CONSTRAINT fk_question_topic
                       FOREIGN KEY (topic_id)
                       REFERENCES topics(id)
                       ON DELETE CASCADE
);

CREATE TABLE answers (
    id BIGSERIAL PRIMARY KEY ,
    question_id BIGINT NOT NULL ,
    answer_text TEXT NOT NULL ,
    answer_index INT NOT NULL ,
    CONSTRAINT fk_answer_question
                       FOREIGN KEY (question_id)
                       REFERENCES questions(id)
                       ON DELETE CASCADE
);

CREATE TABLE test_results (
    id BIGSERIAL PRIMARY KEY ,
    user_id BIGINT NOT NULL ,
    topic_id BIGINT NOT NULL ,
    total_questions INT NOT NULL ,
    correct_answers INT NOT NULL ,
    passed BOOLEAN NOT NULL ,
    finished_at TIMESTAMP NOT NULL,
    CONSTRAINT fk_result_user
                          FOREIGN KEY (user_id)
                          REFERENCES users(id)
                          ON DELETE CASCADE ,
    CONSTRAINT fk_result_topic
                          FOREIGN KEY (topic_id)
                          REFERENCES topics(id)
);

