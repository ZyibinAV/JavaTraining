package com.homeapp.javatraining.tools.migration;

import com.homeapp.javatraining.model.Question;
import com.homeapp.javatraining.model.Topic;

import java.util.List;

public interface JsonQuestionSource {

    List<Question> loadQuestions(Topic topic);
}
