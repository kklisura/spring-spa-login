package com.github.kklisura.spring.spa.controllers.errors.sub;

import com.github.kklisura.spring.spa.controllers.errors.SubError;

/**
 * Validation sub error.
 *
 * @author Kenan Klisura
 */
public class ValidationSubError extends SubError {

  private String fieldName;
  private String message;

  /**
   * Instantiates a new Validation sub error.
   *
   * @param fieldName Field name.
   * @param message   Sub error message.
   */
  public ValidationSubError(String fieldName, String message) {
    this.fieldName = fieldName;
    this.message = message;
  }

  /**
   * Gets field name.
   *
   * @return the field name
   */
  public String getFieldName() {
    return fieldName;
  }

  /**
   * Gets message.
   *
   * @return the message
   */
  public String getMessage() {
    return message;
  }
}
