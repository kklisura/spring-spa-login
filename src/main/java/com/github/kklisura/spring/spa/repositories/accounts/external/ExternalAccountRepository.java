package com.github.kklisura.spring.spa.repositories.accounts.external;

import com.github.kklisura.spring.spa.domain.accounts.Account;
import com.github.kklisura.spring.spa.domain.accounts.external.ExternalAccount;
import java.util.List;
import java.util.Optional;
import org.springframework.data.repository.CrudRepository;

/**
 * External account repository.
 *
 * @author Kenan Klisura
 */
public interface ExternalAccountRepository extends CrudRepository<ExternalAccount, Long> {
  /**
   * Lists all external accounts of a user.
   *
   * @param account Account.
   * @return List of external accounts.
   */
  List<ExternalAccount> findAllByAccount(Account account);

  /**
   * Returns an external account given an account and its external account type.
   *
   * @param externalId External id.
   * @param type       External account type.
   * @return External account.
   */
  Optional<ExternalAccount> findByExternalIdAndType(String externalId, ExternalAccount.Type type);
}
