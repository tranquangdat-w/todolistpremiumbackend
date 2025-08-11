package com.fsoft.dto;

import com.fsoft.utils.PasswordConstraints;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class VerifyAndChangePasswordRequestDto {
    @NotBlank(message = "Email is required")
    private String email;

    @NotBlank(message = "OTP is required")
    private String otp;

    @NotBlank(message = "Old password is required")
    private String oldPassword;

    @NotBlank(message = "New password is required")
    @PasswordConstraints
    private String newPassword;

    @NotBlank(message = "Confirm password is required")
    private String confirmPassword;
}