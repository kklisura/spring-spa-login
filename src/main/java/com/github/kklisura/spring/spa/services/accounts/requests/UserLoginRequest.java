package com.github.kklisura.spring.spa.services.accounts.requests;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * User login request.
 *
 * @author Kenan Klisura
 */
public class UserLoginRequest {
  @NotNull
  @NotBlank
  @Size(max = 255)
  private String emailOrUsername;

  @NotNull
  @NotBlank
  private String password;

  /**
   * Gets email or username.
   *
   * @return the email or username
   */
  public String getEmailOrUsername() {
    return emailOrUsername;
  }

  /**
   * Sets email or username.
   *
   * @param emailOrUsername the email or username
   */
  public void setEmailOrUsername(String emailOrUsername) {
    this.emailOrUsername = emailOrUsername;
  }

  /**
   * Gets password.
   *
   * @return the password
   */
  public String getPassword() {
    return password;
  }

  /**
   * Sets password.
   *
   * @param password the password
   */
  public void setPassword(String password) {
    this.password = password;
  }
}
