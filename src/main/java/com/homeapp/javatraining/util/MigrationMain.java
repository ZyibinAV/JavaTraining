package com.homeapp.javatraining.util;

import com.homeapp.javatraining.model.Topic;
import com.homeapp.javatraining.repository.HibernateQuestionRepository;
import com.homeapp.javatraining.repository.QuestionRepository;
import com.homeapp.javatraining.source.FileQuestionSource;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
@Slf4j
public class MigrationMain {

    public static void main(String[] args) {
        QuestionRepository questionRepository = new HibernateQuestionRepository();
        FileQuestionSource fileQuestionSource = new FileQuestionSource();
        TopicLoader topicLoader = new TopicLoader();

        List<Topic> topics = topicLoader.loadAllTopics();
        if (topics.isEmpty()) {
            throw new IllegalStateException("No topics found in DB");
        }
        QuestionMigrationRunner runner = new QuestionMigrationRunner(questionRepository, fileQuestionSource);
        runner.migrate(topics);

        log.info("Migration completed");
    }
}
