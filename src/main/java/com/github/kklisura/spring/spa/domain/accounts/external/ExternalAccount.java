package com.github.kklisura.spring.spa.domain.accounts.external;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.github.kklisura.spring.spa.domain.accounts.Account;
import java.util.function.Predicate;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

/**
 * External account (social account).
 *
 * @author Kenan Klisura
 */
@Entity
@Table(name = "external_account")
public class ExternalAccount {
  @Id
  @GeneratedValue
  private Long id;

  @JsonIgnore
  @ManyToOne
  @JoinColumn(name = "accounts_id")
  private Account account;

  @NotNull
  @Enumerated(EnumType.STRING)
  private Type type;

  @JsonIgnore
  @NotNull
  @Column(name = "external_id")
  private String externalId;

  /**
   * Gets id.
   *
   * @return the id
   */
  public Long getId() {
    return id;
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
   * Gets account.
   *
   * @return the account
   */
  public Account getAccount() {
    return account;
  }

  /**
   * Sets account.
   *
   * @param account the account
   */
  public void setAccount(Account account) {
    this.account = account;
  }

  /**
   * Gets type.
   *
   * @return the type
   */
  public Type getType() {
    return type;
  }

  /**
   * Sets type.
   *
   * @param type the type
   */
  public void setType(Type type) {
    this.type = type;
  }

  /**
   * Gets external id.
   *
   * @return the external id
   */
  public String getExternalId() {
    return externalId;
  }

  /**
   * Sets external id.
   *
   * @param externalId the external id
   */
  public void setExternalId(String externalId) {
    this.externalId = externalId;
  }

  /**
   * The enum Type.
   */
  public enum Type {
    /**
     * Google type.
     */
    GOOGLE("google"),
    FACEBOOK("facebook"),
    GITHUB("github"),
    TWITTER("twitter");

    private String registrationId;

    Type(String registrationId) {
      this.registrationId = registrationId;
    }

    /**
     * Gets registration id.
     *
     * @return the registration id
     */
    public String getRegistrationId() {
      return registrationId;
    }

    /**
     * Value of by registration id type.
     *
     * @param registrationId Registration id.
     * @return Type.
     */
    public static Type valueOfByRegistrationId(String registrationId) {
      for (Type type : Type.values()) {
        if (type.registrationId.equalsIgnoreCase(registrationId)) {
          return type;
        }
      }

      throw new IllegalArgumentException("No external account type value with registrationId of " +
          registrationId);
    }
  }

  /**
   * Builds a findById predicate.
   *
   * @param id Id.
   * @return FindById predicate.
   */
  public static Predicate<ExternalAccount> findById(Long id) {
    return externalAccount -> id.equals(externalAccount.getId());
  }
}
