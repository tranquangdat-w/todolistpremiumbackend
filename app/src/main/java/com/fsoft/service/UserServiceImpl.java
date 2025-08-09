package com.fsoft.service;

import java.util.Map;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.fsoft.dto.UserDto;
import com.fsoft.exceptions.ApiException;
import com.fsoft.model.User;
import com.fsoft.repository.UserRepository;

import jakarta.transaction.Transactional;
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
  public User registration(
      String username,
      String name,
      String email,
      String password,
      String confirmPassword) {
    this.validateNewUserInfo(username, email, password, confirmPassword);

    final User user = User.builder()
        .username(username)
        .name(name)
        .email(email)
        .password(passwordEncoder.encode(password))
        .verifyToken(UUID.randomUUID())
        .build();

    userRepository.save(user);

    return user;
  }

  private void validateNewUserInfo(
      String username,
      String email,
      String password,
      String confirmPassword) {

    if (!confirmPassword.equals(password)) {
      throw new ApiException(
          String.format("Confirm password and password is not match"),
          HttpStatus.BAD_REQUEST.value());
    }

    final boolean existsUserByEmail = userRepository
        .existsByEmail(email);

    final boolean existsUserByUsername = userRepository
        .existsByUsername(username);

    if (existsUserByUsername) {
      throw new ApiException(
          String.format("Username '%s' is exists", username),
          HttpStatus.UNPROCESSABLE_ENTITY.value());
    }

    if (existsUserByEmail) {
      throw new ApiException(
          String.format("Email '%s' is exists", email),
          HttpStatus.UNPROCESSABLE_ENTITY.value());
    }
  }

  @Transactional
  @Override
  public Map<String, String> verifyAccount(String username, String token) {
    final User existsUserByUsername = userRepository.findByUsername(username);

    if (existsUserByUsername == null) {
      throw new ApiException(
          String.format("Username '%s' is exists", username),
          HttpStatus.UNPROCESSABLE_ENTITY.value());
    }

    if (existsUserByUsername.getVerifyToken() == null &&
        existsUserByUsername.isActive()) {
      throw new ApiException(
          String.format("Account is already active"),
          HttpStatus.BAD_REQUEST.value());
    }

    if (!existsUserByUsername.getVerifyToken().toString().equals(token)) {
      throw new ApiException(
          String.format("Token is not correct"),
          HttpStatus.BAD_REQUEST.value());
    }

    userRepository.verifyAccount(username);

    return Map.of("message", "verify account successfully");
  }

  @Override
  public UserDto loginUser(String email, String password) {
    User exitsUser = userRepository.findByEmail(email);

    if (exitsUser == null) {
      throw new ApiException(
          String.format("User with email %s is not exists", email),
          HttpStatus.BAD_REQUEST.value());
    }

    if (!passwordEncoder.matches(password, exitsUser.getPassword())) {
      throw new ApiException(
          String.format("Password is incorrect"),
          HttpStatus.BAD_REQUEST.value());
    }

    return UserDto.builder()
        .id(exitsUser.getId())
        .email(exitsUser.getEmail())
        .userRole(exitsUser.getUserRole())
        .name(exitsUser.getName())
        .build();
  }
}
