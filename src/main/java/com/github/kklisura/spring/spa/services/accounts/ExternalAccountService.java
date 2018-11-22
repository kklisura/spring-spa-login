package com.github.kklisura.spring.spa.services.accounts;

import com.github.kklisura.spring.spa.configuration.security.services.ExternalAccountInfoSupplier;
import com.github.kklisura.spring.spa.domain.accounts.Account;

/**
 * External account service contract.
 *
 * @author Kenan Klisura
 */
public interface ExternalAccountService {
  /**
   * Load account for externalInfoSupplier. This will either fetch the already linked account or will
   * create a new account and link it to this external account.
   *
   * @param externalAccountInfoSupplier Supplier of the information for external account.
   * @return Account
   */
  Account loadAccount(ExternalAccountInfoSupplier externalAccountInfoSupplier);

  /**
   * Creates the external account and connects it to the specified account.
   *
   * @param account                     Account to connect to.
   * @param externalAccountInfoSupplier External account info supplier.
   */
  void connectAccounts(Account account, ExternalAccountInfoSupplier externalAccountInfoSupplier);
}
