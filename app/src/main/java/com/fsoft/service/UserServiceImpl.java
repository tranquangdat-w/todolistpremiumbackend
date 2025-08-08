package com.fsoft.service;

import java.util.Map;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.fsoft.exceptions.ApiException;
import com.fsoft.exceptions.RegistrationException;
import com.fsoft.model.User;
import com.fsoft.repository.UserRepository;
import com.fsoft.dto.RegistrationRequest;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {
  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;

  @Override
  public User findOneByUserName(String username) {
    return userRepository.findByUsername(username);
  }

  @Override
  public User registration(RegistrationRequest registrationRequest) {
    this.validateNewUserInfo(registrationRequest);

    final User user = User.builder()
        .username(registrationRequest.getUsername())
        .name(registrationRequest.getName())
        .email(registrationRequest.getEmail())
        .password(passwordEncoder.encode(registrationRequest.getPassword()))
        .verifyToken(UUID.randomUUID())
        .build();

    userRepository.save(user);

    return user;
  }

  private void validateNewUserInfo(RegistrationRequest registrationRequest) {
    final boolean existsUserByEmail = userRepository
        .existsByEmail(registrationRequest.getEmail());

    final boolean existsUserByUsername = userRepository
        .existsByUsername(registrationRequest.getUsername());

    if (existsUserByUsername) {
      throw new ApiException(
          String.format("Username '%s' is exists", registrationRequest.getUsername()),
          HttpStatus.UNPROCESSABLE_ENTITY.value());
    }

    if (existsUserByEmail) {
      throw new ApiException(
          String.format("Email '%s' is exists", registrationRequest.getEmail()),
          HttpStatus.UNPROCESSABLE_ENTITY.value());
    }
  }
}
