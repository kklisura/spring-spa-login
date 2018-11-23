package com.github.kklisura.spring.spa.configuration.security.services.impl;

import static com.github.kklisura.spring.spa.utils.stream.FunctionUtils.memoize;

import com.github.kklisura.spring.spa.configuration.security.services.ExternalAccountInfoSupplier;
import com.github.kklisura.spring.spa.configuration.security.twitter.service.TwitterService;
import com.github.kklisura.spring.spa.configuration.security.twitter.service.TwitterService.EnrichedTwitterProfile;
import com.github.kklisura.spring.spa.domain.accounts.external.ExternalAccount.Type;
import com.github.kklisura.spring.spa.services.accounts.requests.AccountCreateRequest;
import java.util.function.Supplier;
import org.springframework.social.oauth1.OAuthToken;
import org.springframework.social.twitter.api.TwitterProfile;

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
    AccountCreateRequest request = new AccountCreateRequest();
    request.setEmail(getExternalEmail());

    final TwitterProfile twitterProfile = enrichedTwitterProfileSupplier.get().getTwitterProfile();
    request.setUsername(twitterProfile.getName());
    request.setDisplayName(twitterProfile.getScreenName());

    return request;
  }
}
