package com.fsoft.exceptions;

import lombok.Getter;

@Getter
public class ApiException extends RuntimeException {
  private Integer statusCode;

  public ApiException(String message, Integer statusCode) {
    super(message);
    this.statusCode = statusCode;
  }
}
