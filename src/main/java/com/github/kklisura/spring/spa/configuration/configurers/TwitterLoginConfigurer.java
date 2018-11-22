package com.github.kklisura.spring.spa.configuration.configurers;

import com.github.kklisura.spring.spa.configuration.security.filters.AuthenticationPreserveFilter;
import com.github.kklisura.spring.spa.configuration.security.twitter.filters.TwitterAuthorizationRequestRedirectFilter;
import com.github.kklisura.spring.spa.configuration.security.twitter.filters.TwitterCallbackFilter;
import com.github.kklisura.spring.spa.configuration.security.twitter.service.TwitterService;
import com.github.kklisura.spring.spa.services.accounts.ExternalAccountService;
import org.springframework.security.config.annotation.web.HttpSecurityBuilder;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestRedirectFilter;

/**
 * Configures a filter chain to support twitter login.
 *
 * @author Kenan Klisura
 */
public class TwitterLoginConfigurer<B extends HttpSecurityBuilder<B>> extends
    AbstractHttpConfigurer<TwitterLoginConfigurer<B>, B> {

  private TwitterService twitterService;
  private ExternalAccountService externalAccountService;

  public TwitterLoginConfigurer(TwitterService twitterService, ExternalAccountService externalAccountService) {
    this.twitterService = twitterService;
    this.externalAccountService = externalAccountService;
  }

  @Override
  public void configure(B builder) throws Exception {
    // Add request redirection filter
    builder.addFilterBefore(new TwitterAuthorizationRequestRedirectFilter(twitterService),
        OAuth2AuthorizationRequestRedirectFilter.class);

    // Add callback filter
    builder.addFilterAfter(new TwitterCallbackFilter(twitterService, externalAccountService),
        TwitterAuthorizationRequestRedirectFilter.class);

    // Add authentication preserver filter
    builder.addFilterBefore(new AuthenticationPreserveFilter(), TwitterAuthorizationRequestRedirectFilter.class);
  }
}
