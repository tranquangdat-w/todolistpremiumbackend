package com.fsoft.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "resend")
public class ResendEmailProperties {
  private String RESEND_API_KEY;
  private String EMAIL_SENDER;
}
