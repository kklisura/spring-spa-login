package com.github.kklisura.spring.spa.repositories.accounts;

import com.github.kklisura.spring.spa.domain.accounts.Account;
import com.github.kklisura.spring.spa.domain.accounts.external.ExternalAccount;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Account repository.
 *
 * @author Kenan Klisura
 */
@Repository
public interface AccountRepository extends CrudRepository<Account, Long>, JpaSpecificationExecutor<Account> {

  /**
   * Finds a user given by email Or username.
   *
   * @param emailOrUsername Email or username.
   * @return Account.
   */
  @Query("from Account where email = :emailOrUsername OR username = :emailOrUsername")
  Optional<Account> findByEmailOrUsername(@Param("emailOrUsername") String emailOrUsername);

  /**
   * Finds an account given by email.
   *
   * @param email Email.
   * @return Account.
   */
  Optional<Account> findByEmail(String email);

  /**
   * Fetches the account given an external id details.
   *
   * @param type External account type.
   * @param externalId External id.
   * @return Account.
   */
  @Query("select a.account from ExternalAccount a where a.type = :type AND a.externalId = :externalId")
  Optional<Account> findByExternalAccount(@Param("type") ExternalAccount.Type type,
      @Param("externalId") String externalId);

  /**
   * Returns true if there is user with given username.
   *
   * @param username Username.
   * @return True if account with given username exists.
   */
  boolean existsByUsername(String username);
}
