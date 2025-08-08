package com.fsoft.security.service;

import java.util.Map;

import com.fsoft.model.User;
import com.fsoft.security.dto.RegistrationRequest;

public interface UserService {
  User findOneByUserName(String username);

  Map<String, String> registration(RegistrationRequest registrationRequest);
}
