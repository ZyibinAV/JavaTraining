package com.homeapp.javatraining.dto.mapper;

import com.homeapp.javatraining.dto.ProfileResponse;
import com.homeapp.javatraining.model.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {
    ProfileResponse toProfileResponse(User user);
}
