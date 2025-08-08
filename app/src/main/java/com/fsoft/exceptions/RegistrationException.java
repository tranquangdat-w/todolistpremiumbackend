package com.fsoft.exceptions;

import lombok.Getter;

@Getter
public class RegistrationException extends RuntimeException {
  public RegistrationException(String message) {
    super(message);
  }
}
