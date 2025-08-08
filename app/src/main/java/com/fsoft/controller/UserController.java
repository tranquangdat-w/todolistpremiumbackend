package com.fsoft.controller;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fsoft.dto.RegistrationRequest;
import com.fsoft.service.UserService;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

@RestController
@AllArgsConstructor
@RequestMapping("/users")
public class UserController {
  private final UserService userService;

  @PostMapping("/register")
  public ResponseEntity<Map<String, String>> registerAccount(
      @Valid @RequestBody RegistrationRequest registrationRequest) {
    Map<String, String> result = userService.registration(registrationRequest);

    return ResponseEntity.status(HttpStatus.CREATED).body(result);
  }
}
