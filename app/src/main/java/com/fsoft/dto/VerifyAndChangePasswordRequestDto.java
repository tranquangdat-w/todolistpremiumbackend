package com.fsoft.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import com.fsoft.utils.PasswordConstraints;

@Data
public class VerifyAndChangePasswordRequestDto {
    @NotBlank(message = "Email is required")
    private String email;

    @NotBlank(message = "OTP is required")
    private String otp;

    @PasswordConstraints
    @NotBlank(message = "New password is required")
    private String newPassword;

    @NotBlank(message = "Confirm password is required")
    private String confirmPassword;
}