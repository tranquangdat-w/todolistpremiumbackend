package com.fsoft.service;

import java.time.Duration;
import java.time.LocalDate;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import com.resend.core.exception.ResendException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.fsoft.dto.ChangePasswordRequestDto;
import com.fsoft.dto.UpdateUserRequestDto;
import com.fsoft.dto.UserDto;
import com.fsoft.exceptions.ApiException;
import com.fsoft.mapper.UserMapper;
import com.fsoft.model.User;
import com.fsoft.model.UserRole;
import com.fsoft.repository.UserRepository;
import com.fsoft.security.jwt.JwtProperties;
import com.fsoft.security.jwt.JwtTokenManager;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class UserService {
  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final JwtProperties jwtProperties;
  private final JwtTokenManager jwtTokenManager;
  private final DropboxService dropboxService;
  private final SendMailService sendMailService;

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
        .createdAt(LocalDate.now())
        .verifyToken(UUID.randomUUID().toString())
        .build();

    userRepository.save(user);

    return user;
  }

  @Transactional
  public Map<String, String> verifyAccount(String username, String token) {

    final User existsUserByUsername = userRepository.findByUsername(username).orElseThrow(() -> new ApiException(
        String.format("Username '%s' is not exists", username),
        HttpStatus.NOT_FOUND.value()));

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

    existsUserByUsername.setVerifyToken(null);

    existsUserByUsername.setActive(true);

    userRepository.save(existsUserByUsername);

    return Map.of("message", "verify account successfully");
  }

  public UserDto loginUser(String email, String password) {
    User exitsUser = userRepository.findByEmail(email)
        .orElseThrow(() -> new ApiException(
            String.format("User with email %s is not exists", email),
            HttpStatus.BAD_REQUEST.value()));

    if (!exitsUser.isActive()) {
      throw new ApiException(
          String.format("Your account has not been activated, please check your mail"),
          HttpStatus.BAD_REQUEST.value());
    }

    if (!passwordEncoder.matches(password, exitsUser.getPassword())) {
      throw new ApiException(
          String.format("Password is incorrect"),
          HttpStatus.BAD_REQUEST.value());
    }

    return UserMapper.toUserDto(exitsUser);
  }

  public ResponseCookie refreshToken(String refreshToken) {
    if (refreshToken == null) {
      throw new ApiException("Please login again", HttpStatus.UNAUTHORIZED.value());
    }

    try {
      DecodedJWT decodedJWT = jwtTokenManager.validateToken(
          refreshToken,
          jwtProperties.getRefreshTokenSecretKey());

      UserDto user = UserDto.builder()
          .id(UUID.fromString(decodedJWT.getClaim("id").asString()))
          .username(decodedJWT.getClaim("username").asString())
          .email(decodedJWT.getClaim("email").asString())
          .userRole(UserRole.valueOf(decodedJWT.getClaim("userRole").asString()))
          .createdAt(LocalDate.parse(decodedJWT.getClaim("createdAt").asString()))
          .active(decodedJWT.getClaim("isActive").asBoolean())
          .build();

      String accessToken = jwtTokenManager.generateToken(
          user,
          jwtProperties.getAccessTokenExpirationMinute(),
          jwtProperties.getAccessTokenSecretKey(),
          jwtProperties.getIssuer());

      ResponseCookie accessTokenRes = ResponseCookie
          .from("accessToken", accessToken)
          .path("/")
          .httpOnly(true)
          .secure(true)
          .maxAge(Duration.ofDays(14))
          .sameSite("none")
          .build();

      return accessTokenRes;

    } catch (Exception exception) {
      throw new ApiException("Please login again", HttpStatus.UNAUTHORIZED.value());
    }
  }

  @Transactional
  public UserDto updateUser(UUID userId, MultipartFile avatarFile) {
    User user = getUserByIdOrThrow(userId);

    try {
      String url = dropboxService
          .uploadImage(avatarFile, userId)
          .orElseThrow(
              () -> new ApiException("Some error occur when upload avatar"));

      user.setAvatar(url);

      userRepository.save(user);

      return UserMapper.toUserDto(user);
    } catch (Exception e) {
      System.out.println(e);
      e.printStackTrace();
      throw new ApiException(e.getMessage());
    }
  }

  @Transactional
  public UserDto updateUser(UUID userId, UpdateUserRequestDto updateUserRequestDto) {

    User user = getUserByIdOrThrow(userId);

    if (updateUserRequestDto.getName() != null) {
      user.setName(updateUserRequestDto.getName());
    }

    userRepository.save(user);

    return UserMapper.toUserDto(user);
  }

  @Transactional
  public void changePassword(UUID userId, ChangePasswordRequestDto changePasswordRequestDto) {
    User user = getUserByIdOrThrow(userId);

    if (!passwordEncoder.matches(changePasswordRequestDto.getOldPassword(), user.getPassword())) {
      throw new ApiException(
          String.format("Password is incorrect"),
          HttpStatus.BAD_REQUEST.value());
    }

    if (!changePasswordRequestDto.getNewPassword().equals(changePasswordRequestDto.getConfirmPassword())) {
      throw new ApiException(
          String.format("New password and confirm password do not match!"),
          HttpStatus.BAD_REQUEST.value());
    }

    user.setPassword(passwordEncoder.encode(changePasswordRequestDto.getNewPassword()));

    userRepository.save(user);
  }

  private User getUserByIdOrThrow(UUID userId) {
    return userRepository.findById(userId)
        .orElseThrow(() -> new ApiException("User not found", HttpStatus.NOT_FOUND.value()));
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

  public void sendForgotPasswordOtp(String email) throws ResendException {
    User user = userRepository.findByEmail(email).
            orElseThrow(() -> new ApiException(
                    String.format("User with email %s is not exists", email),
                    HttpStatus.BAD_REQUEST.value()));

    String otp = String.format("%06d", new Random().nextInt(999999));

    user.setOtp(otp);
    userRepository.save(user);

    sendMailService.sendForgotPasswordMail(user.getEmail(), user.getUsername(), otp);
  }

  @Transactional
  public Map<String, String> verifyOtpAndChangePassword(String email, String otp, ChangePasswordRequestDto changePasswordRequestDto) {
    User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new ApiException(
                    String.format("User with email %s is not exists", email),
                    HttpStatus.BAD_REQUEST.value()));

    if (!otp.equals(user.getOtp())) {
      throw new ApiException("Invalid OTP", HttpStatus.BAD_REQUEST.value());
    }

    changePassword(user.getId(), changePasswordRequestDto);

    user.setOtp(null);
    userRepository.save(user);

    return Map.of("message", "OTP verified and password changed successfully");
  }
}
