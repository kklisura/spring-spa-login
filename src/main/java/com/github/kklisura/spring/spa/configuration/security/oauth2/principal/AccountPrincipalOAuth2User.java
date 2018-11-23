package com.github.kklisura.spring.spa.configuration.security.oauth2.principal;

import com.github.kklisura.spring.spa.configuration.security.principal.AccountPrincipal;
import com.github.kklisura.spring.spa.domain.accounts.Account;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

/**
 * Account principal for OAuth2 user.
 *
 * @author Kenan Klisura
 */
public class AccountPrincipalOAuth2User extends AccountPrincipal implements OAuth2User {
  private OAuth2User oAuth2User;

  /**
   * Instantiates a new account principal with OAuth2 user.
   *
   * @param account    Account.
   * @param oAuth2User OAuth2 user.
   */
  public AccountPrincipalOAuth2User(Account account, OAuth2User oAuth2User) {
    super(account);
    this.oAuth2User = oAuth2User;
  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return Collections.emptyList();
  }

  @Override
  public Map<String, Object> getAttributes() {
    return oAuth2User.getAttributes();
  }

  /**
   * Gets the user.
   *
   * @return OAuth2 user.
   */
  public OAuth2User getOAuth2User() {
    return oAuth2User;
  }
}
