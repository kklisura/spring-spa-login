package com.github.kklisura.spring.spa.configuration.security.oauth2.service.types;

/**
 * User email.
 *
 * @author Kenan Klisura
 */
public class UserEmail {
  private String email;

  private boolean verified;

  private boolean primary;

  private String visibility;

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

  /**
   * Is verified boolean.
   *
   * @return the boolean
   */
  public boolean isVerified() {
    return verified;
  }

  /**
   * Sets verified.
   *
   * @param verified the verified
   */
  public void setVerified(boolean verified) {
    this.verified = verified;
  }

  /**
   * Is primary boolean.
   *
   * @return the boolean
   */
  public boolean isPrimary() {
    return primary;
  }

  /**
   * Sets primary.
   *
   * @param primary the primary
   */
  public void setPrimary(boolean primary) {
    this.primary = primary;
  }

  /**
   * Gets visibility.
   *
   * @return the visibility
   */
  public String getVisibility() {
    return visibility;
  }

  /**
   * Sets visibility.
   *
   * @param visibility the visibility
   */
  public void setVisibility(String visibility) {
    this.visibility = visibility;
  }
}
