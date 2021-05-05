package ru.spbstu.exception;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
  @ExceptionHandler(RuntimeException.class)
  @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
  public GlobalApiErrorResponse generalRuntimeException(RuntimeException re) {
    return new GlobalApiErrorResponse(HttpStatus.UNPROCESSABLE_ENTITY, "Something went wrong: " + re.getMessage());
  }

  @ExceptionHandler(DataIntegrityViolationException.class)
  @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
  public GlobalApiErrorResponse dataIntegrityViolationException(DataIntegrityViolationException ve) {
    return new GlobalApiErrorResponse(HttpStatus.UNPROCESSABLE_ENTITY, "Data integrity violation: " + ve.getMessage());
  }

  @ExceptionHandler(FileProcessingException.class)
  @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
  public GlobalApiErrorResponse fileProcessingException(FileProcessingException fe) {
    return new GlobalApiErrorResponse(HttpStatus.UNPROCESSABLE_ENTITY, fe.getMessage());
  }
}
