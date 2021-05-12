package ru.spbstu.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.http.HttpStatus;

@Data
@AllArgsConstructor
@Accessors(chain = true)
public class GlobalApiErrorResponse {
  private HttpStatus httpStatus;
  private String message;

}
