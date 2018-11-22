package com.github.kklisura.spring.spa.controllers.errors;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

/**
 * API error handling taken from https://www.toptal.com/java/spring-boot-rest-api-error-handling.
 *
 * @author Kenan Klisura
 */
public class Error {

  private HttpStatus status;

  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy hh:mm:ss")
  private LocalDateTime timestamp;

  private String message;

  @JsonInclude(value = Include.NON_EMPTY)
  private String debugMessage;

  @JsonInclude(value = Include.NON_EMPTY)
  private List<SubError> subErrors;

  private Error() {
    timestamp = LocalDateTime.now();
  }

  /**
   * Instantiates a new error.
   *
   * @param status Http status.
   */
  public Error(HttpStatus status) {
    this();
    this.status = status;
  }

  /**
   * Instantiates a new Error.
   *
   * @param status  Http status.
   * @param message Message.
   */
  public Error(HttpStatus status, String message) {
    this();
    this.status = status;
    this.message = message;
  }

  /**
   * Instantiates a new error.
   *
   * @param status Http status.
   * @param ex     Exception cause.
   */
  public Error(HttpStatus status, Throwable ex) {
    this();
    this.status = status;
    this.message = "Unexpected error";
    this.debugMessage = ex.getLocalizedMessage();
  }

  /**
   * Instantiates a new Error.
   *
   * @param status  Http status.
   * @param message Message.
   * @param ex      Exception cause.
   */
  public Error(HttpStatus status, String message, Throwable ex) {
    this();
    this.status = status;
    this.message = message;
    this.debugMessage = ex.getLocalizedMessage();
  }

  /**
   * Gets code.
   *
   * @return the code
   */
  public int getCode() {
    return status.value();
  }

  /**
   * Gets status.
   *
   * @return the status
   */
  public HttpStatus getStatus() {
    return status;
  }

  /**
   * Gets timestamp.
   *
   * @return the timestamp
   */
  public LocalDateTime getTimestamp() {
    return timestamp;
  }

  /**
   * Gets message.
   *
   * @return the message
   */
  public String getMessage() {
    return message;
  }

  /**
   * Gets debug message.
   *
   * @return the debug message
   */
  public String getDebugMessage() {
    return debugMessage;
  }

  /**
   * Gets sub errors.
   *
   * @return the sub errors
   */
  public List<SubError> getSubErrors() {
    return subErrors;
  }

  /**
   * Creates a response entity from an error.
   *
   * @return the response entity
   */
  public ResponseEntity<Error> toResponseEntity() {
    return ResponseEntity.status(getStatus()).body(this);
  }

  /**
   * Adds a sub-error.
   *
   * @param subError Sub error.
   */
  public Error addSubError(SubError subError) {
    if (subErrors == null) {
      subErrors = new ArrayList<>();
    }
    this.subErrors.add(subError);
    return this;
  }
}
