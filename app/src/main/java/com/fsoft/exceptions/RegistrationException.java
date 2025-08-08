package com.fsoft.exceptions;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class RegistrationException extends RuntimeException {
  private final String errorMessage;
}
