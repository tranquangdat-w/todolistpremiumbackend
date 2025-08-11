package com.fsoft.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Component
@EnableConfigurationProperties
@ConfigurationProperties(prefix = "dropbox")
public class DropboxProperties {
  private String REFRESH_TOKEN_API_DROP_BOX;
  private String APP_KEY;
  private String APP_SECRET;
}
