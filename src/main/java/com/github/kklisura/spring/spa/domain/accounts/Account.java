package com.github.kklisura.spring.spa.domain.accounts;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.github.kklisura.spring.spa.domain.accounts.external.ExternalAccount;
import com.github.kklisura.spring.spa.domain.accounts.external.ExternalAccount_;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
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

  @JsonIgnore
  private String password;

  @NotNull
  @Column(name = "display_name")
  private String displayName;

  @OneToMany(fetch = FetchType.EAGER, mappedBy = ExternalAccount_.ACCOUNT)
  private Set<ExternalAccount> externalAccounts;

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

  /**
   * Gets external accounts.
   *
   * @return the external accounts
   */
  public Set<ExternalAccount> getExternalAccounts() {
    return externalAccounts;
  }

  /**
   * Sets external accounts.
   *
   * @param externalAccounts the external accounts
   */
  public void setExternalAccounts(
      Set<ExternalAccount> externalAccounts) {
    this.externalAccounts = externalAccounts;
  }
}
