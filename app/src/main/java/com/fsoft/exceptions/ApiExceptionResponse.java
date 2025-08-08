package com.fsoft.exceptions;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ApiExceptionResponse {
  private String message;

  private String stackTrace;
}
