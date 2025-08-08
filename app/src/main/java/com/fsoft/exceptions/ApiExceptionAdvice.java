package com.fsoft.exceptions;

import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
public class ApiExceptionAdvice {

  @ExceptionHandler({ ApiException.class })
  ResponseEntity<ApiExceptionResponse> handleRegistrationException(ApiException exception) {
    final ApiExceptionResponse errorResponse = ApiExceptionResponse.builder()
        .message(exception.getMessage())
        .build();

    return ResponseEntity.status(exception.getStatusCode()).body(errorResponse);
  }

  @ExceptionHandler({ MethodArgumentNotValidException.class })
  public final ResponseEntity<ApiExceptionResponse> handleMethodArgumentNotValidException(
      MethodArgumentNotValidException exception) {

    final List<FieldError> fieldErrors = exception.getBindingResult().getFieldErrors();
    final List<String> errorList = fieldErrors.stream().map(DefaultMessageSourceResolvable::getDefaultMessage)
        .collect(Collectors.toList());

    final ApiExceptionResponse validationErrorResponse = ApiExceptionResponse.builder()
        .message(String.join(", ", errorList))
        .stackTrace(getStackTraceAsString(exception))
        .build();

    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(validationErrorResponse);
  }

  // Nếu exception không được bắt thì mặc định là lỗi của server
  @ExceptionHandler(Exception.class)
  ResponseEntity<ApiExceptionResponse> handleRegistrationException(Exception exception) {
    final ApiExceptionResponse commonError = ApiExceptionResponse.builder()
        .message(exception.getMessage())
        .stackTrace(getStackTraceAsString(exception))
        .build();

    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(commonError);
  }

  private String getStackTraceAsString(Throwable throwable) {
    StringWriter sw = new StringWriter();
    PrintWriter pw = new PrintWriter(sw);
    throwable.printStackTrace(pw);
    return sw.toString();
  }
}
