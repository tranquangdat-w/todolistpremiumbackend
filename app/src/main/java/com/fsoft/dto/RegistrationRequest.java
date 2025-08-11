package com.fsoft.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class RegistrationRequest {
  @NotEmpty(message = "Your name cannot be empty")
  @NotNull(message = "Name cannot be null")
  private String name;

  @Email(message = "Email is invalid")
  @NotEmpty(message = "Email cannot be empty")
  @NotNull(message = "Email cannot be null")
  private String email;

  @NotEmpty(message = "Your usename cannot be empty")
  @NotNull(message = "Username cannot be null")
  private String username;

  @NotEmpty(message = "Password cannot be empty")
  @NotNull(message = "Password cannot be null")
  private String password;

  @NotEmpty(message = "Confirm password cannot be empty")
  @NotNull(message = "Confirm password cannot be null")
  private String confirmPassword;
}
