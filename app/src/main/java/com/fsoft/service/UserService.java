package com.fsoft.service;

import com.fsoft.dto.UserDto;
import com.fsoft.model.User;

import java.util.Map;

public interface UserService {
  User findOneByUserName(String username);

  User registration(
      String username,
      String name,
      String email,
      String password,
      String confirmPassword);

  Map<String, String> verifyAccount(String username, String token);

  UserDto loginUser(String email, String password);
}
