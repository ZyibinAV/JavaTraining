package com.homeapp.javatraining.tools.migration;

import com.homeapp.javatraining.config.ApplicationConfig;
import com.homeapp.javatraining.model.Topic;
import com.homeapp.javatraining.repository.QuestionRepository;
import com.homeapp.javatraining.util.QuestionMigrationRunner;
import com.homeapp.javatraining.util.TopicLoader;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * Executor for data migration from JSON to PostgreSQL.
 * This class orchestrates the migration process by:
 * 1. Initializing dependencies via ApplicationConfig
 * 2. Loading all topics from database
 * 3. Running QuestionMigrationRunner to migrate questions
 */
@Slf4j
public class DataMigrationExecutor {

    public static void main(String[] args) {
        log.info("Starting data migration from JSON to PostgreSQL...");

        try {
            // Initialize application configuration
            log.info("Initializing ApplicationConfig...");
            ApplicationConfig config = new ApplicationConfig();

            // Get required dependencies
            QuestionRepository questionRepository = config.getQuestionRepository();
            TopicLoader topicLoader = config.getTopicLoader();
            JsonQuestionImportSource jsonQuestionImportSource = new JsonQuestionImportSource();

            // Create migration runner
            QuestionMigrationRunner migrationRunner = new QuestionMigrationRunner(
                questionRepository,
                jsonQuestionImportSource
            );

            // Load all topics from database
            log.info("Loading topics from database...");
            List<Topic> topics = topicLoader.loadAllTopics();
            
            if (topics.isEmpty()) {
                log.error("No topics found in database. Cannot proceed with migration.");
                System.exit(1);
            }
            
            log.info("Found {} topics: {}", topics.size(), 
                topics.stream().map(Topic::getCode).toList());
            
            // Execute migration
            log.info("Executing migration...");
            migrationRunner.migrate(topics);
            
            log.info("Data migration completed successfully!");
            
        } catch (Exception e) {
            log.error("Data migration failed", e);
            System.exit(1);
        }
    }
}
