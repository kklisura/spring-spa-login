package com.github.kklisura.spring.spa.configuration.security.twitter;

import com.github.kklisura.spring.spa.configuration.security.principal.AccountPrincipal;
import java.util.Collections;
import org.springframework.security.authentication.AbstractAuthenticationToken;

/**
 * OAuth1 twitter authentication token.
 *
 * @author Kenan Klisura
 */
public class TwitterAuthenticationToken extends AbstractAuthenticationToken {
  private AccountPrincipal accountPrincipal;

  public TwitterAuthenticationToken(AccountPrincipal accountPrincipal) {
    super(Collections.emptyList());
    this.accountPrincipal = accountPrincipal;
  }

  @Override
  public Object getCredentials() {
    return null;
  }

  @Override
  public Object getPrincipal() {
    return accountPrincipal;
  }
}
