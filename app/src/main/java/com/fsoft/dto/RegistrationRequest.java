package com.fsoft.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class RegistrationRequest {
  @NotEmpty(message = "Your name cannot be empty")
  private String name;

  @Email(message = "Email is invalid")
  @NotEmpty(message = "Email cannot be empty")
  private String email;

  @NotEmpty(message = "Your usename cannot be empty")
  private String username;

  @NotEmpty(message = "Password cannot be empty")
  private String password;

  @NotEmpty(message = "Confirm password cannot be empty")
  private String confirmPassword;
}
