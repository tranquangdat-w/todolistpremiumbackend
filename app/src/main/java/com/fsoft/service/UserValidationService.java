package com.fsoft.service;

import org.springframework.stereotype.Service;

import com.fsoft.repository.UserRepository;
import com.fsoft.security.dto.RegistrationRequest;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class UserValidationService {
  private static final String EMAIL_ALREADY_EXISTS = "email_already_exists";

  private static final String USERNAME_ALREADY_EXISTS = "username_already_exists";

  private final UserRepository userRepository;

  public void validateUser(RegistrationRequest registrationRequest) {
    final String username = registrationRequest.getUsername();
    final String email = registrationRequest.getEmail();

    checkEmail(email);
    checkUserName(username);
  }

  private void checkEmail(String email) {
    final boolean existsUser = userRepository.existsByEmail(email);
  }

  private void checkUserName(String username) {

  }
}
