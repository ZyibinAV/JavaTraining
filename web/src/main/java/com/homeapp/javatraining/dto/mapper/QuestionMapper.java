package com.homeapp.javatraining.dto.mapper;

import com.homeapp.javatraining.dto.QuestionDTO;
import com.homeapp.javatraining.model.Question;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = AnswerMapper.class)
public interface QuestionMapper {
    @Mapping(target = "topicCode", source = "topic.code")
    QuestionDTO toQuestionDTO(Question question);
}
