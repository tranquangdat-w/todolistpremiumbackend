package com.fsoft.service;

import org.springframework.stereotype.Service;

import static com.fsoft.utils.Constrants.CONTENT_MAIL_VERIFY_ACCOUNT;
import static com.fsoft.utils.Constrants.WEBSITE_DOMAIN_DEV;

import com.fsoft.configuration.ResendEmailProperties;
import com.resend.Resend;
import com.resend.core.exception.ResendException;
import com.resend.services.emails.model.CreateEmailOptions;
import com.resend.services.emails.model.CreateEmailResponse;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Service
@AllArgsConstructor
public class SendMailService {
  private ResendEmailProperties resendEmailProperties;

  public void sendVerifyAccountMail(
      final String receiver,
      final String username,
      final String verifyToken) throws ResendException {
    String verificationLink = String.format(
        "%s/users/verfication?username=%s&token=%s",
        WEBSITE_DOMAIN_DEV,
        username,
        verifyToken);

    String htmlContent = String.format(
        CONTENT_MAIL_VERIFY_ACCOUNT,
        username,
        verificationLink);

    String subject = "HUS-TEAM: Verify your account to using our service";

    sendMail(subject, receiver, htmlContent);
  }

  public void sendForgotPasswordMail(String email, String username, String otp) throws ResendException {
    String htmlContent = """
        <!DOCTYPE html>
        <html lang="en">
          <head>
            <meta charset="UTF-8" />
            <title>OTP Verification</title>
            <style>
              body {
                font-family: Arial, sans-serif;
                background-color: #f4f6f8;
                margin: 0;
                padding: 0;
              }
              .container {
                max-width: 600px;
                margin: 30px auto;
                background: #ffffff;
                border-radius: 10px;
                padding: 20px 30px;
                box-shadow: 0 4px 8px rgba(0,0,0,0.1);
              }
              h2 {
                color: #1d3545;
              }
              .otp {
                font-size: 28px;
                font-weight: bold;
                color: #ff5722;
                margin: 20px 0;
                text-align: center;
                letter-spacing: 6px;
              }
              .footer {
                margin-top: 30px;
                font-size: 12px;
                color: #888888;
                text-align: center;
              }
            </style>
          </head>
          <body>
            <div class="container">
              <h2>Hello, %s</h2>
              <p>We received a request to reset your password. Use the OTP code below to continue:</p>
              <div class="otp">%s</div>
              <p>This OTP is valid for <b>5 minutes</b>. If you did not request a password reset, please ignore this email.</p>
              <div class="footer">
                © %d VNU-TEAM. All rights reserved.
              </div>
            </div>
          </body>
        </html>
        """
        .formatted(username, otp, java.time.LocalDate.now().getYear());

    String subject = "VNU-TEAM: Below is the OTP code to reset password.";
    sendMail(subject, email, htmlContent);
  }

  private void sendMail(
      final String subject,
      final String receiver,
      final String content) throws ResendException {

    Resend resend = new Resend(resendEmailProperties.getRESEND_API_KEY());

    final CreateEmailOptions params = CreateEmailOptions.builder()
        .from(resendEmailProperties.getEMAIL_SENDER())
        .to(receiver)
        .subject(subject)
        .html(content)
        .build();

    final CreateEmailResponse data = resend.emails().send(params);
  }
}
