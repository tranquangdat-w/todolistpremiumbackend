package com.fsoft.mapper;

import org.springframework.stereotype.Component;

import com.fsoft.dto.UserDto;
import com.fsoft.model.User;

@Component
public class UserMapper {

  public UserDto toDto(User user) {
    if (user == null) {
      return null;
    }

    UserDto dto = new UserDto();
    dto.setId(user.getId());
    dto.setName(user.getName());
    dto.setUsername(user.getUsername());
    dto.setEmail(user.getEmail());
    dto.setUserRole(user.getUserRole());
    dto.setActive(user.isActive());
    dto.setAvatar(user.getAvatar());
    return dto;
  }
}
