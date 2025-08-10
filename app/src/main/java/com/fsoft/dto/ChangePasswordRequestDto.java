package com.fsoft.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ChangePasswordRequestDto {
  @NotNull
  private String oldPassword;

  @NotNull
  private String newPassword;

  @NotNull
  private String cofirmPassword;
}
