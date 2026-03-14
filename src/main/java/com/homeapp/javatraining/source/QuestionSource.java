package com.homeapp.javatraining.source;

import com.homeapp.javatraining.model.Question;
import com.homeapp.javatraining.model.Topic;

import java.util.List;

public interface QuestionSource {

    List<Question> loadQuestions(Topic topic);
}
