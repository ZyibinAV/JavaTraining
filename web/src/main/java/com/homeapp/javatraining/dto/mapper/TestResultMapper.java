package com.homeapp.javatraining.dto.mapper;

import com.homeapp.javatraining.dto.TestResultDTO;
import com.homeapp.javatraining.model.TestResult;
import com.homeapp.javatraining.model.Topic;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface TestResultMapper {
    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "topicCodes", source = "topics", qualifiedByName = "topicsToCodes")
    TestResultDTO toTestResultDTO(TestResult testResult);

    @Named("topicsToCodes")
    default Set<String> topicsToCodes(Set<Topic> topics) {
        return topics.stream()
                .map(Topic::getCode)
                .collect(Collectors.toSet());
    }
}
