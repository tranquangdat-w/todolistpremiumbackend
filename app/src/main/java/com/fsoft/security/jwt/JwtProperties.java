package com.fsoft.security.jwt;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Component
@EnableConfigurationProperties
@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {
  private String issuer;
  private String accessTokenSecretKey;
  private String refreshTokenSecretKey;
  private long accessTokenExpirationMinute;
  private long refreshTokenExpirationMinute;
}
