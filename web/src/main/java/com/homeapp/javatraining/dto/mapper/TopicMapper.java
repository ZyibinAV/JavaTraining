package com.homeapp.javatraining.dto.mapper;

import com.homeapp.javatraining.dto.TopicDTO;
import com.homeapp.javatraining.model.Topic;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface TopicMapper {

    TopicDTO toTopicDTO(Topic topic);
    List<TopicDTO> toTopicDTOList(List<Topic> topics);
}
