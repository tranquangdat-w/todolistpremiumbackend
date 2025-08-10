package com.fsoft.configuration;

import java.util.HashMap;
import java.util.Map;

import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.DelegatingPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class PasswordEncoderConfiguration {

  @Bean
  public PasswordEncoder passwordEncoder() {
    Map<String, PasswordEncoder> encoders = new HashMap<>();

    PasswordEncoder argon2PasswordEncoder = new Argon2PasswordEncoder(16, 32, 1, 65536, 3);

    PasswordEncoder bcryptPasswordEncoder = new BCryptPasswordEncoder();

    final String idForEncode = "argon2";

    encoders.put(idForEncode, argon2PasswordEncoder);

    encoders.put("bycrypt", bcryptPasswordEncoder);

    return new DelegatingPasswordEncoder(idForEncode, encoders);
  }
}
