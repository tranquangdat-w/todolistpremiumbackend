package com.fsoft.service;

import com.fsoft.model.User;
import com.fsoft.dto.RegistrationRequest;

public interface UserService {
  User findOneByUserName(String username);

  User registration(RegistrationRequest registrationRequest);
}
