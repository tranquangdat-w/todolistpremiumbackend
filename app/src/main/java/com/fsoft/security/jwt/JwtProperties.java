package com.fsoft.security.jwt;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Getter;
import lombok.Setter;

@Getter
@Configuration
@Setter
@EnableConfigurationProperties
@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {
  private String issuer;
  private String accessTokenSecretKey;
  private String refreshTokenSecretKey;
  private long accessTokenExpirationMinute;
  private long refreshTokenExpirationMinute;
}
