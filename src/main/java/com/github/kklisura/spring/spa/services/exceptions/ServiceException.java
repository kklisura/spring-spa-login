package com.github.kklisura.spring.spa.services.exceptions;

/**
 * Base service exception.
 *
 * @author Kenan Klisura
 */
public abstract class ServiceException extends Exception {
  /**
   * Instantiates a new Service exception.
   */
  public ServiceException() {
  }

  /**
   * Instantiates a new Service exception.
   *
   * @param message the message
   */
  public ServiceException(String message) {
    super(message);
  }

  /**
   * Instantiates a new Service exception.
   *
   * @param message the message
   * @param cause the cause
   */
  public ServiceException(String message, Throwable cause) {
    super(message, cause);
  }
}
