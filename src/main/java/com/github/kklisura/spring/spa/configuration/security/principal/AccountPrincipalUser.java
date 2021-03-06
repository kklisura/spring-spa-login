package com.github.kklisura.spring.spa.configuration.security.principal;

import com.github.kklisura.spring.spa.domain.accounts.Account;
import java.util.Collection;
import java.util.Collections;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * Account principal for account authenticated with username-password combination.
 *
 * @author Kenan Klisura
 */
public class AccountPrincipalUser extends AccountPrincipal implements UserDetails {

  /**
   * Ctor.
   */
  public AccountPrincipalUser() {
    super();
    // Empty ctor.
  }

  /**
   * Instantiates a new Account principal.
   *
   * @param account Account.
   */
  public AccountPrincipalUser(Account account) {
    super(account);
  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return Collections.emptyList();
  }

  @Override
  public String getPassword() {
    return getAccount().getPassword();
  }

  @Override
  public String getUsername() {
    return getAccount().getUsername();
  }

  @Override
  public boolean isAccountNonExpired() {
    return true;
  }

  @Override
  public boolean isAccountNonLocked() {
    return true;
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return true;
  }

  @Override
  public boolean isEnabled() {
    return true;
  }
}
