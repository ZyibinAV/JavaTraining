package com.homeapp.javatraining.util;

import com.homeapp.javatraining.model.Topic;
import com.homeapp.javatraining.repository.HibernateQuestionRepository;
import com.homeapp.javatraining.repository.HibernateTopicRepository;
import com.homeapp.javatraining.repository.QuestionRepository;
import com.homeapp.javatraining.repository.TopicRepository;
import com.homeapp.javatraining.tools.migration.JsonQuestionImportSource;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
@Slf4j
public class MigrationMain {

    public static void main(String[] args) {
        QuestionRepository questionRepository = new HibernateQuestionRepository();
        TopicRepository topicRepository = new HibernateTopicRepository();
        JsonQuestionImportSource fileQuestionSource = new JsonQuestionImportSource();
        TopicLoader topicLoader = new TopicLoader(topicRepository);

        List<Topic> topics = topicLoader.loadAllTopics();
        if (topics.isEmpty()) {
            throw new IllegalStateException("No topics found in DB");
        }
        QuestionMigrationRunner runner = new QuestionMigrationRunner(questionRepository, fileQuestionSource);
        runner.migrate(topics);

        log.info("Migration completed");
    }
}
