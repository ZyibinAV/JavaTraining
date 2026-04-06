package com.homeapp.javatraining.util;


import com.homeapp.javatraining.model.Question;
import com.homeapp.javatraining.model.Topic;
import com.homeapp.javatraining.repository.QuestionRepository;
import com.homeapp.javatraining.source.FileQuestionSource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class QuestionMigrationRunner {

    private final QuestionRepository questionRepository;
    private final FileQuestionSource fileQuestionSource;

    public void migrate(List<Topic> topics) {
        for (Topic topic : topics) {
            if (topic.getId() == null) {
                throw new IllegalStateException(
                        "Topic must be loaded from DB, but got transient: " + topic.getCode()
                );
            }
            log.info("Starting migration for topic={}", topic.getCode());
            List<Question> questions = fileQuestionSource.loadQuestions(topic);
            List<Question> toSave = new ArrayList<>();

            int skippedCount = 0;

            for (Question question : questions) {
                boolean exists = questionRepository.existsByTextAndTopic(
                        question.getQuestionText(), topic);

                if (exists) {
                    skippedCount++;
                    continue;
                }
                toSave.add(question);
            }
            questionRepository.saveAll(toSave);

            log.info("Migration completed for topic={}, saved={}, skipped={}",
                    topic.getCode(), toSave.size(), skippedCount);
        }
        log.info("ALL QUESTIONS MIGRATED SUCCESSFULLY");
    }
}
