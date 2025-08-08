package com.fsoft.exceptions;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@Getter
@Setter
@Builder
public class ApiExceptionResponse {

  private HttpStatus status;

  private String message;

  private String stackTrace;

}
