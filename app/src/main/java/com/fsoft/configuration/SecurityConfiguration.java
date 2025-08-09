package com.fsoft.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfiguration {
  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    return http
        .csrf(CsrfConfigurer::disable)
        .authorizeHttpRequests(
            // Allow some endpoint
            request -> request.requestMatchers(
                "/users/register",
                "/users/login",
                "/users/verify",
                "/users/logout",
                "/users/refresh_token").permitAll().anyRequest().authenticated())
        // Using JWT insted of session management
        .sessionManagement(
            manager -> manager.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        // .exceptionHandling(handler -> handler.authenticationEntryPoint())
        .build();
  }
}
