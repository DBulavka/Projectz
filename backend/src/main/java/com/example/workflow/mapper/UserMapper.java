package com.example.workflow.mapper;

import com.example.workflow.dto.auth.UserDto;
import com.example.workflow.entity.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserDto toDto(User user);
}
