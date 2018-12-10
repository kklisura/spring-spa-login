package com.github.kklisura.spring.spa.configuration.security.principal;

import com.github.kklisura.spring.spa.domain.accounts.Account;
import org.springframework.security.core.AuthenticatedPrincipal;

/**
 * Account principal just wraps an account.
 *
 * @author Kenan Klisura
 */
public class AccountPrincipal implements AuthenticatedPrincipal {
  private Account account;

  /**
   * Ctor.
   */
  public AccountPrincipal() {
    // Empty ctor.
  }

  /**
   * Instantiates a new Account principal.
   *
   * @param account Account.
   */
  public AccountPrincipal(Account account) {
    this.account = account;
  }

  public Account getAccount() {
    return account;
  }

  @Override
  public String getName() {
    return account.getDisplayName();
  }
}
