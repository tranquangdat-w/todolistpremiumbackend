package com.fsoft.controller;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fsoft.dto.RegistrationRequest;
import com.fsoft.model.User;
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

  @PostMapping("/register")
  public ResponseEntity<Map<String, String>> registerAccount(
      @Valid @RequestBody RegistrationRequest registrationRequest) throws ResendException {
    User newUser = userService.registration(registrationRequest);

    sendMailService.sendVerifyAccountMail(
        newUser.getEmail(),
        newUser.getUsername(),
        newUser.getVerifyToken().toString());

    return ResponseEntity.status(HttpStatus.CREATED).body(
        Map.of("message", "Create new account successfully"));
  }
}
