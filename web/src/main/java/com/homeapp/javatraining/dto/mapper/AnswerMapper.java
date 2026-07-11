package com.homeapp.javatraining.dto.mapper;

import com.homeapp.javatraining.dto.AnswerDTO;
import com.homeapp.javatraining.model.Answer;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface AnswerMapper {
    @Mapping(target = "index", source = "answerIndex")
    @Mapping(target = "text", source = "answerText")
    AnswerDTO toAnswerDTO(Answer answer);
    List<AnswerDTO> toAnswerDTOList(List<Answer> answers);
}
