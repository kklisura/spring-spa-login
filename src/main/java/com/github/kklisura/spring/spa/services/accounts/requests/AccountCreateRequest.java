package com.github.kklisura.spring.spa.services.accounts.requests;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

/**
 * AccountCreateRequest used for creating new account.
 *
 * @author Kenan Klisura
 */
public class AccountCreateRequest {

  @NotEmpty(message = "Username should not be empty.")
  @Size(max = 255, message = "Username should be up to 255 characters.")
  private String username;

  @NotEmpty(message = "Email should not be empty.")
  @Email(message = "Invalid email provided.")
  @Size(max = 255)
  private String email;

  @NotEmpty(message = "Display name should not be empty.")
  @Size(max = 255)
  private String displayName;

  @NotEmpty
  private String password;

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

  /**
   * Gets full name.
   *
   * @return the full name
   */
  public String getDisplayName() {
    return displayName;
  }

  /**
   * Sets full name.
   *
   * @param displayName the full name
   */
  public void setDisplayName(String displayName) {
    this.displayName = displayName;
  }

  /**
   * Gets username.
   *
   * @return the username
   */
  public String getUsername() {
    return username;
  }

  /**
   * Sets username.
   *
   * @param username the username
   */
  public void setUsername(String username) {
    this.username = username;
  }

  /**
   * Gets email.
   *
   * @return the email
   */
  public String getEmail() {
    return email;
  }

  /**
   * Sets email.
   *
   * @param email the email
   */
  public void setEmail(String email) {
    this.email = email;
  }
}
