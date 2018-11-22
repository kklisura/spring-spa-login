package com.github.kklisura.spring.spa.services.accounts;

import com.github.kklisura.spring.spa.domain.accounts.Account;
import com.github.kklisura.spring.spa.domain.accounts.external.ExternalAccount;
import com.github.kklisura.spring.spa.domain.accounts.external.ExternalAccount.Type;
import com.github.kklisura.spring.spa.services.accounts.exceptions.NoPasswordSetServiceException;
import com.github.kklisura.spring.spa.services.accounts.requests.AccountCreateRequest;
import com.github.kklisura.spring.spa.services.exceptions.EntityNotFoundServiceException;
import java.util.Optional;
import org.springframework.security.core.userdetails.UserDetailsService;

/**
 * Created by Kenan Klisura on 22/11/2018.
 *
 * @author Kenan Klisura
 */
public interface AccountService extends UserDetailsService, AccountUsernameGeneratorService {
  /**
   * Finds an account given by external info.
   *
   * @param type       External type.
   * @param externalId External id.
   * @return Account. optional
   */
  Optional<Account> findByExternalAccount(ExternalAccount.Type type, String externalId);

  /**
   * Finds an account given by external info.
   *
   * @param email the email
   * @return Account. optional
   */
  Optional<Account> findByEmail(String email);

  /**
   * Create external account.
   *
   * @param account    Account.
   * @param type       External account type.
   * @param externalId External account id.
   */
  void createExternalAccount(Account account, Type type, String externalId);

  /**
   * Creates an account given a request.
   *
   * @param request Request.
   * @return Created account.
   */
  Account createAccount(AccountCreateRequest request);

  /**
   * Returns external account for account and external account type.
   *
   * @param externalId          External id.
   * @param externalAccountType External account type.
   * @return External account.
   */
   Optional<ExternalAccount> getExternalAccount(String externalId, Type externalAccountType);

  /**
   * Links an account and external account.
   *
   * @param externalAccount External account.
   * @param account         Account.
   */
   void linkExternalAccountWithAccount(ExternalAccount externalAccount, Account account);

  /**
   * Unlinks an external account from an account.
   *
   * @param id                Account id.
   * @param externalAccountId External account id.
   * @throws EntityNotFoundServiceException If no account found.
   * @throws NoPasswordSetServiceException  If no password set while unlinking last account.
   */
   void unlinkExternalAccountFromAccount(Long id, Long externalAccountId)
      throws EntityNotFoundServiceException, NoPasswordSetServiceException;
}
