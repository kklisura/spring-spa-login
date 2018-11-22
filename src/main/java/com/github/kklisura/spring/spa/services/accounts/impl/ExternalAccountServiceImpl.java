package com.github.kklisura.spring.spa.services.accounts.impl;

import static com.github.kklisura.spring.spa.utils.stream.FunctionUtils.memoize;

import com.github.kklisura.spring.spa.configuration.security.services.ExternalAccountInfoSupplier;
import com.github.kklisura.spring.spa.domain.accounts.Account;
import com.github.kklisura.spring.spa.domain.accounts.external.ExternalAccount;
import com.github.kklisura.spring.spa.domain.accounts.external.ExternalAccount.Type;
import com.github.kklisura.spring.spa.services.accounts.AccountService;
import com.github.kklisura.spring.spa.services.accounts.ExternalAccountService;
import com.github.kklisura.spring.spa.services.accounts.requests.AccountCreateRequest;
import java.util.Optional;
import java.util.function.Supplier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * External account service implementation.
 *
 * @author Kenan Klisura
 */
@Service
public class ExternalAccountServiceImpl implements ExternalAccountService {

  private static final Logger LOGGER = LoggerFactory.getLogger(ExternalAccountService.class);

  private AccountService accountService;

  /**
   * Instantiates a new external account service.
   *
   * @param accountService Account service.
   */
  @Autowired
  public ExternalAccountServiceImpl(AccountService accountService) {
    this.accountService = accountService;
  }

  /**
   * Load account for externalInfoSupplier. This will either fetch the already linked account or will
   * create a new account and link it to this external account.
   *
   * @param externalAccountInfoSupplier Supplier of the information for external account.
   * @return Account
   */
  public Account loadAccount(ExternalAccountInfoSupplier externalAccountInfoSupplier) {
    Type type = externalAccountInfoSupplier.getType();
    String externalId = externalAccountInfoSupplier.getExternalId();
    Supplier<String> email = memoize(externalAccountInfoSupplier::getExternalEmail);

    // Find an account via this external id and type
    Optional<Account> optionalAccount = accountService.findByExternalAccount(type, externalId);
    return optionalAccount.map(Optional::of).orElseGet(() ->
        // ...if the account is not found, find it by email
        accountService.findByEmail(email.get())
            // ...if its there, create an external account for future references
            .map(account -> createExternalAccount(account, type, externalId)))
        // If no account available create completely new account
        .orElseGet(() -> createAccount(externalAccountInfoSupplier));
  }

  private Account createAccount(ExternalAccountInfoSupplier externalAccountInfoSupplier) {
    AccountCreateRequest createRequest = externalAccountInfoSupplier.createAccountRequest();
    Account account = accountService.createAccount(createRequest);
    accountService.createExternalAccount(account, externalAccountInfoSupplier.getType(),
        externalAccountInfoSupplier.getExternalId());
    return account;
  }

  private Account createExternalAccount(Account account, Type type, String sub) {
    accountService.createExternalAccount(account, type, sub);
    return account;
  }

  /**
   * Creates the external account and connects it to the specified account.
   *
   * @param account                     Account to connect to.
   * @param externalAccountInfoSupplier External account info supplier.
   */
  public void connectAccounts(Account account, ExternalAccountInfoSupplier externalAccountInfoSupplier) {
    Type type = externalAccountInfoSupplier.getType();
    String externalId = externalAccountInfoSupplier.getExternalId();

    Optional<ExternalAccount> optionalExternalAccount = accountService.getExternalAccount(externalId, type);

    if (optionalExternalAccount.isPresent()) {
      accountService.linkExternalAccountWithAccount(optionalExternalAccount.get(), account);
    } else {
      accountService.createExternalAccount(account, type, externalId);
    }
  }
}
