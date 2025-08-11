package com.fsoft.mapper;

import com.fsoft.dto.UserDto;
import com.fsoft.model.User;

public final class UserMapper {

  private UserMapper() {
  }

  public static UserDto toUserDto(User user) {
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
    dto.setCreatedAt(user.getCreatedAt());
    return dto;
  }
}
