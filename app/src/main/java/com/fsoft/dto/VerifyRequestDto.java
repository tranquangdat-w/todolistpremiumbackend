package com.fsoft.dto;

import org.hibernate.validator.constraints.UUID;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class VerifyRequestDto {
  @NotEmpty(message = "Your use_name cannot be empty")
  private String username;

  @NotEmpty(message = "Token can't be empty")
  @UUID(message = "Token is invalid value")
  private String token;
}
