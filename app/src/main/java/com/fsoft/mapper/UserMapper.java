package com.fsoft.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.fsoft.dto.UserDto;
import com.fsoft.model.User;

@Mapper(componentModel = "spring")
public interface UserMapper {
  UserDto toDto(User user);
}
