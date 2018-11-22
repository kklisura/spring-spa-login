package com.github.kklisura.spring.spa.configuration.security.services.impl;

import static com.github.kklisura.spring.spa.utils.stream.FunctionUtils.memoize;

import com.github.kklisura.spring.spa.configuration.security.services.ExternalAccountInfoSupplier;
import com.github.kklisura.spring.spa.configuration.security.twitter.service.TwitterService;
import com.github.kklisura.spring.spa.configuration.security.twitter.service.TwitterService.EnrichedTwitterProfile;
import com.github.kklisura.spring.spa.domain.accounts.external.ExternalAccount.Type;
import com.github.kklisura.spring.spa.services.accounts.requests.AccountCreateRequest;
import java.util.function.Supplier;
import org.springframework.social.oauth1.OAuthToken;

/**
 * Wraps a twitter external info account supplier.
 *
 * @author Kenan Klisura
 */
public class TwitterExternalAccountInfoSupplier implements ExternalAccountInfoSupplier {
  private Supplier<EnrichedTwitterProfile> enrichedTwitterProfileSupplier;

  /**
   * Instantiates a new account info supplier.
   *
   * @param oAuthToken     OAuth token.
   * @param twitterService Twitter service.
   */
  public TwitterExternalAccountInfoSupplier(OAuthToken oAuthToken, TwitterService twitterService) {
    this.enrichedTwitterProfileSupplier = memoize(() -> twitterService.getUserInfo(oAuthToken));
  }

  @Override
  public Type getType() {
    return Type.TWITTER;
  }

  @Override
  public String getExternalId() {
    return Long.toString(this.enrichedTwitterProfileSupplier.get().getTwitterProfile().getId());
  }

  @Override
  public String getExternalEmail() {
    return this.enrichedTwitterProfileSupplier.get().getEmail();
  }

  @Override
  public AccountCreateRequest createAccountRequest() {
    return null;
  }
}
