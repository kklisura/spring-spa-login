package com.github.kklisura.spring.spa.configuration.security.expression;

import com.github.kklisura.spring.spa.configuration.security.principal.AccountPrincipal;
import com.github.kklisura.spring.spa.domain.accounts.Account;
import com.github.kklisura.spring.spa.services.accounts.RoleTypes;
import java.util.Optional;
import org.springframework.security.access.expression.SecurityExpressionRoot;
import org.springframework.security.access.expression.method.MethodSecurityExpressionOperations;
import org.springframework.security.core.Authentication;

/**
 * Abstract base class for custom security SpEL methods.
 *
 * @author Kenan Klisura
 */
public abstract class AbstractSecurityExpression extends SecurityExpressionRoot implements
    MethodSecurityExpressionOperations {

  private Object filterObject;
  private Object returnObject;
  private Object target;

  /**
   * Creates a new instance
   *
   * @param authentication the {@link Authentication} to use. Cannot be null.
   */
  protected AbstractSecurityExpression(Authentication authentication) {
    super(authentication);
  }

  @Override
  public void setFilterObject(Object filterObject) {
    this.filterObject = filterObject;
  }

  @Override
  public Object getFilterObject() {
    return filterObject;
  }

  @Override
  public void setReturnObject(Object returnObject) {
    this.returnObject = returnObject;
  }

  @Override
  public Object getReturnObject() {
    return returnObject;
  }

  @Override
  public Object getThis() {
    return target;
  }

  public void setThis(Object target) {
    this.target = target;
  }

  protected Optional<AccountPrincipal> getAccountPrincipal() {
    final Object principal = getPrincipal();
    if (principal != null && principal instanceof AccountPrincipal) {
      return Optional.of((AccountPrincipal) principal);
    }

    return Optional.empty();
  }

  protected Optional<Account> getAccount() {
    return getAccountPrincipal()
        .map(AccountPrincipal::getAccount);
  }

  protected boolean isAdmin() {
    return hasRole(RoleTypes.ROLE_ADMIN);
  }
}
