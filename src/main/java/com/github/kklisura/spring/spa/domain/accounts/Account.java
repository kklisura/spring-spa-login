package com.github.kklisura.spring.spa.domain.accounts;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.github.kklisura.spring.spa.services.accounts.Role;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

/**
 * Account class.
 *
 * @author Kenan Klisura
 */
@Entity
@Table(name = "accounts")
public class Account {
  @Id
  @GeneratedValue
  private Long id;

  @NotNull
  private String username;

  @NotNull
  private String email;

  // NOTE(kklisura): JsonIgnore is here, just to make sure if, for any reason, Account instance
  // leaks as JSON, no password will be shown.
  @JsonIgnore
  private String password;

  @NotNull
  @Column(name = "display_name")
  private String displayName;

  @Column(name = "role")
  @Enumerated(value = EnumType.STRING)
  private Role role = Role.MEMBER;

  /**
   * Ctor.
   */
  public Account() {
    // Empty ctor.
  }

  /**
   * Gets id.
   *
   * @return the id
   */
  public Long getId() {
    return id;
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


  /**
   * Sets id.
   *
   * @param id the id
   */
  public void setId(Long id) {
    this.id = id;
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
   * Gets roleName.
   *
   * @return the roleName
   */
  public Role getRole() {
    return role;
  }

  /**
   * Sets roleName.
   *
   * @param role the roleName
   */
  public void setRole(Role role) {
    this.role = role;
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
   * Returns true if this account has password.
   *
   * @return True if this account has password.
   */
  @JsonIgnore
  public boolean hasPasswordSet() {
    return getPassword() != null;
  }
}
