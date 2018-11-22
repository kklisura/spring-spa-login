package com.github.kklisura.spring.spa.configuration.security.services;

import com.github.kklisura.spring.spa.domain.accounts.external.ExternalAccount.Type;
import com.github.kklisura.spring.spa.services.accounts.requests.AccountCreateRequest;

/**
 * External user info supplier returns information of the external user. It'a wrapper around OAuth2 and OAuth1 auth
 * result. This is used since there are different objects returned as oauth1 (twitter) and oauth2 (fb, google, etc) and
 * the idea is to have similar flow in {@link com.github.kklisura.spring.spa.services.accounts.ExternalAccountService}.
 *
 * @author Kenan Klisura
 */
public interface ExternalAccountInfoSupplier {
  /**
   * Returns type of the external account.
   *
   * @return Type.
   */
  Type getType();

  /**
   * Returns an external id for this external user info supplier.
   *
   * @return External id.
   */
  String getExternalId();

  /**
   * Returns an email.
   *
   * @return Email.
   */
  String getExternalEmail();

  /**
   * Creates an account creation request. This is used to pass to service to create an account.
   *
   * @return Account create request.
   */
  AccountCreateRequest createAccountRequest();
}
