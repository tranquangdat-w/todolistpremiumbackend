package com.fsoft.dto;

import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class LoginRequestDto {
  @NonNull
  @Email(message = "Email is invalid")
  private String email;

  @NonNull
  private String password;
}
