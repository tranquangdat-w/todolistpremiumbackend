package com.fsoft.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import com.fsoft.utils.PasswordConstraints;

@Setter
@Getter
public class ChangePasswordRequestDto {
  @NotNull
  @PasswordConstraints
  private String oldPassword;

  @NotNull
  @PasswordConstraints
  private String newPassword;

  @NotNull
  @PasswordConstraints
  private String confirmPassword;
}
