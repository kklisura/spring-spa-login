package com.github.kklisura.spring.spa.configuration.security.expression;

import com.github.kklisura.spring.spa.domain.accounts.Account;
import java.util.Optional;
import org.springframework.security.core.Authentication;

/**
 * List of custom methods used in SpEL expressions - these methods are used in @PostAuthorize/@PreAuthorize.
 *
 * @author Kenan Klisura
 */
public class MethodsSecurityExpression extends AbstractSecurityExpression {
  /**
   * Creates a new instance
   *
   * @param authentication the {@link Authentication} to use. Cannot be null.
   */
  public MethodsSecurityExpression(Authentication authentication) {
    super(authentication);
  }

  /**
   * Is the authenticated user owner of the account.
   *
   * @param accountId Account id.
   * @return True if user is either admin or owner.
   */
  public boolean isAccountOwner(Long accountId) {
    if (isAdmin()) {
      return true;
    }

    if (accountId != null) {
      final Optional<Long> currentAccountId = getAccount().map(Account::getId);
      return currentAccountId.isPresent() && accountId.equals(currentAccountId.get());
    }

    return false;
  }
}
