package com.fsoft.controller;

import com.fsoft.dto.*;
import org.springframework.http.HttpHeaders;
import java.time.Duration;
import java.util.Map;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.fsoft.model.User;
import com.fsoft.security.jwt.JwtPayload;
import com.fsoft.security.jwt.JwtProperties;
import com.fsoft.security.jwt.JwtTokenManager;
import com.fsoft.service.SendMailService;
import com.fsoft.service.UserService;
import com.fsoft.utils.ImageValidator;
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

    String accessToken = jwtTokenManager.generateToken(
        user,
        jwtProperties.getAccessTokenExpirationMinute(),
        jwtProperties.getAccessTokenSecretKey(),
        jwtProperties.getIssuer());

    String refreshToken = jwtTokenManager.generateToken(
        user,
        jwtProperties.getRefreshTokenExpirationMinute(),
        jwtProperties.getRefreshTokenSecretKey(),
        jwtProperties.getIssuer());

    ResponseCookie accessTokenRes = ResponseCookie
        .from("accessToken", accessToken)
        .path("/")
        .httpOnly(true)
        .secure(true)
        .maxAge(Duration.ofDays(14))
        .sameSite("none")
        .build();

    ResponseCookie refreshTokenRes = ResponseCookie
        .from("refreshToken", refreshToken)
        .httpOnly(true)
        .path("/")
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

  @GetMapping("/refresh_token")
  public ResponseEntity<Map<String, String>> refresh_token(
      @CookieValue(value = "refreshToken", required = false) String refreshToken) {

    ResponseCookie accessTokenRes = userService.refreshToken(refreshToken);

    return ResponseEntity
        .status(HttpStatus.OK)
        .header(HttpHeaders.SET_COOKIE, accessTokenRes.toString())
        .body(Map.of("message", "refresh token successfully"));
  }

  @PutMapping
  public ResponseEntity<UserDto> updateUser(
      @Valid @RequestBody UpdateUserRequestDto updateUserRequestDto,
      @AuthenticationPrincipal JwtPayload jwtPayload) {

    UUID userId = jwtPayload.getId();

    UserDto updatedUser = userService.updateUser(
        userId,
        updateUserRequestDto);

    return ResponseEntity.status(HttpStatus.OK).body(updatedUser);
  }

  @PutMapping("/avatar")
  public ResponseEntity<UserDto> updateUser(
      @RequestPart(value = "avatar", required = false) MultipartFile avatarFile,
      @AuthenticationPrincipal JwtPayload jwtPayload) {

    UUID userId = jwtPayload.getId();

    ImageValidator.validateAvatar(avatarFile);

    UserDto updatedUser = userService.updateUser(userId, avatarFile);

    return ResponseEntity.status(HttpStatus.OK).body(updatedUser);
  }

  @PutMapping("/change_password")
  public ResponseEntity<Map<String, String>> changePassword(
      @RequestBody ChangePasswordRequestDto changePasswordRequestDto,
      @AuthenticationPrincipal JwtPayload jwtPayload) {
    UUID userId = jwtPayload.getId();

    userService.changePassword(userId, changePasswordRequestDto);

    return ResponseEntity.status(HttpStatus.OK).body(
        Map.of("message", "change password successfully"));
  }

  @PostMapping("/send-otp")
  public ResponseEntity<Map<String, String>> sendOtp(
          @Valid @RequestBody ForgotPasswordRequestDto requestDto) throws ResendException{

    userService.sendForgotPasswordOtp(requestDto.getEmail());
    return ResponseEntity.ok(Map.of("message", "OTP send to your email"));
  }

  @PostMapping("/verify-otp-and-change-password")
  public ResponseEntity<Map<String, String>> verifyOtpAndChangePassword(
          @Valid @RequestBody VerifyAndChangePasswordRequestDto request) {
    ChangePasswordRequestDto changePasswordRequestDto = new ChangePasswordRequestDto();
    changePasswordRequestDto.setOldPassword(request.getOldPassword());
    changePasswordRequestDto.setNewPassword(request.getNewPassword());
    changePasswordRequestDto.setConfirmPassword(request.getConfirmPassword());

    Map<String, String> result = userService.verifyOtpAndChangePassword(
            request.getEmail(), request.getOtp(), changePasswordRequestDto);
    return ResponseEntity.ok(result);
  }
}
