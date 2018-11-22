package com.github.kklisura.spring.spa.configuration.configurers;

import com.github.kklisura.spring.spa.configuration.security.filters.AuthenticationPreserveFilter;
import com.github.kklisura.spring.spa.configuration.security.oauth2.handlers.OAuth2FailureHandler;
import com.github.kklisura.spring.spa.configuration.security.oauth2.handlers.OAuth2SuccessHandler;
import com.github.kklisura.spring.spa.services.accounts.AccountUsernameGeneratorService;
import com.github.kklisura.spring.spa.services.accounts.ExternalAccountService;
import org.springframework.security.config.annotation.web.HttpSecurityBuilder;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.oauth2.client.OAuth2LoginConfigurer;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestRedirectFilter;

/**
 * Created by Kenan Klisura on 16/09/2018.
 *
 * @param <B> the type parameter
 * @author Kenan Klisura
 */
public class OAuth2Configurer<B extends HttpSecurityBuilder<B>> extends AbstractHttpConfigurer<OAuth2Configurer<B>, B> {

  private OAuth2LoginConfigurer<B> oAuth2LoginConfigurer = new OAuth2LoginConfigurer<>();
  private ExternalAccountService externalAccountService;
  private OAuth2AuthorizedClientService oAuth2AuthorizedClientService;
  private AccountUsernameGeneratorService accountUsernameGeneratorService;

  /**
   * Instantiates a new configurer.
   *
   * @param externalManagementService     External account service.
   * @param oAuth2AuthorizedClientService Authorized client service.
   */
  public OAuth2Configurer(ExternalAccountService externalManagementService,
      OAuth2AuthorizedClientService oAuth2AuthorizedClientService,
      AccountUsernameGeneratorService accountUsernameGeneratorService) {
    this.externalAccountService = externalManagementService;
    this.oAuth2AuthorizedClientService = oAuth2AuthorizedClientService;
    this.accountUsernameGeneratorService = accountUsernameGeneratorService;
  }

  @Override
  public void init(B builder) throws Exception {
    oAuth2LoginConfigurer.setBuilder(builder);
    oAuth2LoginConfigurer.init(builder);
  }

  @Override
  public void configure(B builder) throws Exception {
    // Success and failure handler redirects user to some URL and restores previous authentication if it exists.
    oAuth2LoginConfigurer
        .successHandler(
            new OAuth2SuccessHandler(externalAccountService, oAuth2AuthorizedClientService,
                accountUsernameGeneratorService))
        .failureHandler(new OAuth2FailureHandler());

    // Configure default oAuth2LoginConfigurer
    oAuth2LoginConfigurer.setBuilder(builder);
    oAuth2LoginConfigurer.configure(builder);

    // Add filter to preserve previous authentication if available
    builder.addFilterBefore(new AuthenticationPreserveFilter(), OAuth2AuthorizationRequestRedirectFilter.class);
  }
}
