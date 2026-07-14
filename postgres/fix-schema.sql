-- Исправление схемы test_results: удалить старую колонку topic_id,
-- создать join-таблицу для @ManyToMany с Topic
ALTER TABLE test_results DROP COLUMN IF EXISTS topic_id;

CREATE TABLE IF NOT EXISTS test_results_topics (
    test_result_id BIGINT NOT NULL REFERENCES test_results(id) ON DELETE CASCADE,
    topic_id BIGINT NOT NULL REFERENCES topics(id) ON DELETE CASCADE,
    PRIMARY KEY (test_result_id, topic_id)
);
