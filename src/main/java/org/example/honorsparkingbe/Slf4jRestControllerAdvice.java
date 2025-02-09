package org.example.honorsparkingbe;

import java.util.stream.Collectors;
import org.apache.catalina.connector.Response;
import org.example.honorsparkingbe.dto.error.ErrorResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class Slf4jRestControllerAdvice {

  private Logger logger = LoggerFactory.getLogger(Slf4jRestControllerAdvice.class);

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorResponse> exception(Exception exception) {
    logger.error("Internal Server Exception: {}", exception.getMessage());
    ErrorResponse errorResponse = ErrorResponse.builder()
        .code(Response.SC_INTERNAL_SERVER_ERROR)
        .message(exception.getMessage())
        .build();
    return ResponseEntity.internalServerError().body(errorResponse);
  }

  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<ErrorResponse> illegalArgumentException(
      IllegalArgumentException exception) {
    logger.error("IllegalArgumentException: {}", exception.getMessage());

    ErrorResponse errorResponse = ErrorResponse.builder()
        .code(Response.SC_BAD_REQUEST)
        .message(exception.getMessage())
        .build();
    return ResponseEntity.badRequest().body(errorResponse);
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ErrorResponse> handleRequestValidExceptions(
      MethodArgumentNotValidException ex) {
    String errorMessage = ex.getBindingResult()
        .getAllErrors()
        .stream()
        .map(DefaultMessageSourceResolvable::getDefaultMessage)
        .collect(Collectors.joining(", "));

    ErrorResponse errorResponse = ErrorResponse.builder()
        .code(Response.SC_BAD_REQUEST)
        .message(errorMessage)
        .build();

    return ResponseEntity
        .badRequest()
        .body(errorResponse);
  }
}
