package com.fsoft.controller;

import org.springframework.http.HttpHeaders;
import java.time.Duration;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fsoft.dto.LoginRequestDto;
import com.fsoft.dto.RegistrationRequest;
import com.fsoft.dto.UserDto;
import com.fsoft.dto.VerifyRequestDto;
import com.fsoft.model.User;
import com.fsoft.security.jwt.JwtProperties;
import com.fsoft.security.jwt.JwtTokenManager;
import com.fsoft.service.SendMailService;
import com.fsoft.service.UserService;
import com.resend.core.exception.ResendException;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

@RestController
@AllArgsConstructor
@RequestMapping("/users")
public class UserController {
  private final UserService userService;
  private final SendMailService sendMailService;
  private final JwtProperties jwtProperties;
  private final JwtTokenManager jwtTokenManager;

  @PostMapping("/register")
  public ResponseEntity<Map<String, String>> registerAccount(
      @Valid @RequestBody RegistrationRequest registrationRequest) throws ResendException {
    User newUser = userService.registration(
        registrationRequest.getUsername(),
        registrationRequest.getName(),
        registrationRequest.getEmail(),
        registrationRequest.getPassword(),
        registrationRequest.getConfirmPassword());

    sendMailService.sendVerifyAccountMail(
        newUser.getEmail(),
        newUser.getUsername(),
        newUser.getVerifyToken().toString());

    return ResponseEntity.status(HttpStatus.CREATED).body(
        Map.of("message", "Create new account successfully"));
  }

  @PutMapping("/verify")
  public ResponseEntity<Map<String, String>> verifyAccount(
      @Valid @RequestBody VerifyRequestDto verifyRequestDto) {

    Map<String, String> result = userService.verifyAccount(
        verifyRequestDto.getUsername(),
        verifyRequestDto.getToken());

    return ResponseEntity.status(HttpStatus.OK).body(result);
  }

  @PostMapping("/login")
  public ResponseEntity<UserDto> loginUser(
      @Valid @RequestBody LoginRequestDto loginRequestDto) {
    UserDto user = userService.loginUser(
        loginRequestDto.getEmail(),
        loginRequestDto.getPassword());

    String accessToken = jwtTokenManager.genrateToken(
        user,
        jwtProperties.getAccessTokenExpirationMinute(),
        jwtProperties.getAccessTokenSecretKey(),
        jwtProperties.getIssuer());

    String refreshToken = jwtTokenManager.genrateToken(
        user,
        jwtProperties.getRefreshTokenExpirationMinute(),
        jwtProperties.getRefreshTokenSecretKey(),
        jwtProperties.getIssuer());

    ResponseCookie accessTokenRes = ResponseCookie
        .from("accessToken", accessToken)
        .httpOnly(true)
        .secure(true)
        .maxAge(Duration.ofDays(14))
        .sameSite("none")
        .build();

    ResponseCookie refreshTokenRes = ResponseCookie
        .from("refreshToken", refreshToken)
        .httpOnly(true)
        .secure(true)
        .maxAge(Duration.ofDays(14))
        .sameSite("none")
        .build();

    return ResponseEntity
        .status(HttpStatus.OK)
        .header(HttpHeaders.SET_COOKIE, accessTokenRes.toString())
        .header(HttpHeaders.SET_COOKIE, refreshTokenRes.toString())
        .body(user);
  }

  @DeleteMapping("/logout")
  public ResponseEntity<Map<String, String>> logoutUser() {
    ResponseCookie accessTokenRes = ResponseCookie
        .from("accessToken", "")
        .maxAge(0)
        .build();

    ResponseCookie refreshTokenRes = ResponseCookie
        .from("refreshToken", "")
        .maxAge(0)
        .build();

    return ResponseEntity
        .status(HttpStatus.OK)
        .header(HttpHeaders.SET_COOKIE, accessTokenRes.toString())
        .header(HttpHeaders.SET_COOKIE, refreshTokenRes.toString())
        .body(Map.of("message", "logout user successfully"));
  }
}
