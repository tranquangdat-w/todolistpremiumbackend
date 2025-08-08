package com.fsoft.service;

import org.hibernate.validator.cfg.defs.pl.REGONDef;
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
  private final ResendEmailProperties resendEmailProperties;

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
