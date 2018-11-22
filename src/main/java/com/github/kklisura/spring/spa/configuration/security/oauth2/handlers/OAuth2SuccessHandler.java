package com.github.kklisura.spring.spa.configuration.security.oauth2.handlers;

import static com.github.kklisura.spring.spa.configuration.security.filters.AuthenticationPreserveFilter.clearPreviousAuthentication;
import static com.github.kklisura.spring.spa.configuration.security.filters.AuthenticationPreserveFilter.getPreviousAuthentication;
import static com.github.kklisura.spring.spa.configuration.security.filters.AuthenticationPreserveFilter.hasPreviousAuthentication;
import static com.github.kklisura.spring.spa.configuration.security.filters.AuthenticationPreserveFilter.restorePreviousAuthentication;
import static com.github.kklisura.spring.spa.controllers.external.login.ExternalLoginController.getExternalLoginBaseUrl;

import com.github.kklisura.spring.spa.configuration.security.oauth2.principal.AccountPrincipalOAuth2User;
import com.github.kklisura.spring.spa.configuration.security.principal.AccountPrincipal;
import com.github.kklisura.spring.spa.configuration.security.services.ExternalAccountInfoSupplier;
import com.github.kklisura.spring.spa.configuration.security.services.impl.OAuth2ExternalAccountInfoSupplier;
import com.github.kklisura.spring.spa.controllers.external.login.ExternalLoginController;
import com.github.kklisura.spring.spa.domain.accounts.Account;
import com.github.kklisura.spring.spa.domain.accounts.external.ExternalAccount.Type;
import com.github.kklisura.spring.spa.services.accounts.AccountUsernameGeneratorService;
import com.github.kklisura.spring.spa.services.accounts.ExternalAccountService;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;

/**
 * Handles the OAuth2 authentication success by either linking the external account to an existing account or by creating
 * a new account. After successful handle it redirects user to {@link ExternalLoginController#SUCCESS_URL}.
 *
 * @author Kenan Klisura
 */
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
  private ExternalAccountService externalAccountService;
  private OAuth2AuthorizedClientService oAuth2AuthorizedClientService;
  private AccountUsernameGeneratorService usernameGeneratorService;

  public OAuth2SuccessHandler(ExternalAccountService externalAccountService,
      OAuth2AuthorizedClientService oAuth2AuthorizedClientService,
      AccountUsernameGeneratorService usernameGeneratorService) {
    super(getExternalLoginBaseUrl() + ExternalLoginController.SUCCESS_URL);
    this.externalAccountService = externalAccountService;
    this.oAuth2AuthorizedClientService = oAuth2AuthorizedClientService;
    this.usernameGeneratorService = usernameGeneratorService;
  }

  @Override
  public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
      Authentication authentication) throws IOException, ServletException {
    final SecurityContext securityContext = SecurityContextHolder.getContext();

    final OAuth2AuthenticationToken authenticationToken = (OAuth2AuthenticationToken) authentication;
    final OAuth2User oAuth2User = authenticationToken.getPrincipal();

    final Type externalAccountType = Type
        .valueOfByRegistrationId(authenticationToken.getAuthorizedClientRegistrationId());

    boolean isConnectingAccounts = hasPreviousAuthentication(request);

    if (isConnectingAccounts) {
      Authentication previousAuthentication = getPreviousAuthentication(request);
      AccountPrincipal previousPrincipal = (AccountPrincipal) previousAuthentication.getPrincipal();

      // Wrap the oAuth2 account supplier
      ExternalAccountInfoSupplier externalAccountInfoSupplier = new OAuth2ExternalAccountInfoSupplier(
          oAuth2User, externalAccountType, usernameGeneratorService
      );

      // Connect account with its external account.
      externalAccountService.connectAccounts(previousPrincipal.getAccount(), externalAccountInfoSupplier);

      restorePreviousAuthentication(request);
    } else {
      // Get the oAuth2 authorized client so we can potentially fetch additional info
      OAuth2AuthorizedClient oAuth2AuthorizedClient = oAuth2AuthorizedClientService
          .loadAuthorizedClient(authenticationToken.getAuthorizedClientRegistrationId(), oAuth2User.getName());

      // Wrap the oAuth2 account supplier
      ExternalAccountInfoSupplier externalAccountInfoSupplier = new OAuth2ExternalAccountInfoSupplier(
          oAuth2User, oAuth2AuthorizedClient, usernameGeneratorService
      );

      // Loads an account for this external login. This will either create new account or link/match with existing account
      // and will return that account.
      Account account = externalAccountService.loadAccount(externalAccountInfoSupplier);

      // Create an account principal with loaded account and oAuthUser either from the last one or previous one.
      AccountPrincipalOAuth2User accountPrincipal = new AccountPrincipalOAuth2User(account,
          oAuth2User instanceof AccountPrincipalOAuth2User ?
              ((AccountPrincipalOAuth2User) oAuth2User).getOAuth2User() :
              oAuth2User
      );

      // Create new authentication token and save it
      OAuth2AuthenticationToken newOAuth2AuthenticationToken = new OAuth2AuthenticationToken(
          accountPrincipal,
          accountPrincipal.getAuthorities(),
          authenticationToken.getAuthorizedClientRegistrationId()
      );
      securityContext.setAuthentication(newOAuth2AuthenticationToken);
    }

    clearPreviousAuthentication(request);
    clearAuthenticationAttributes(request);

    super.onAuthenticationSuccess(request, response, securityContext.getAuthentication());
  }
}
