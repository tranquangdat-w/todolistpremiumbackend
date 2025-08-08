package com.fsoft.security.service;

import java.util.Map;

import org.springframework.stereotype.Service;

import com.fsoft.exceptions.RegistrationException;
import com.fsoft.model.User;
import com.fsoft.repository.UserRepository;
import com.fsoft.security.dto.RegistrationRequest;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {
  private final UserRepository userRepository;

  @Override
  public User findOneByUserName(String username) {
    return userRepository.findByUsername(username);
  }

  @Override
  public Map<String, String> registration(RegistrationRequest registrationRequest) {
    this.validateNewUserInfo(registrationRequest);

    final User user = User.builder()
        .username(registrationRequest.getUsername())
        .name(registrationRequest.getName())
        .email(registrationRequest.getEmail())
        .build();

    System.out.println(user);

    userRepository.save(user);

    return Map.of("message", "registration successfully");
  }

  private void validateNewUserInfo(RegistrationRequest registrationRequest) {
    final boolean existsUserByEmail = userRepository
        .existsByEmail(registrationRequest.getEmail());

    final boolean existsUserByUsername = userRepository
        .existsByUsername(registrationRequest.getUsername());

    if (existsUserByUsername) {
      throw new RegistrationException(
          String.format("Username '%s' is exists", registrationRequest.getUsername()));
    }

    if (existsUserByEmail) {
      throw new RegistrationException(
          String.format("Email '%s' is exists", registrationRequest.getEmail()));
    }

  }

}
