package com.github.kklisura.spring.spa.configuration.security.twitter.filters;

import static com.github.kklisura.spring.spa.configuration.security.filters.AuthenticationPreserveFilter.clearPreviousAuthentication;
import static com.github.kklisura.spring.spa.configuration.security.filters.AuthenticationPreserveFilter.getPreviousAuthentication;
import static com.github.kklisura.spring.spa.configuration.security.filters.AuthenticationPreserveFilter.hasPreviousAuthentication;
import static com.github.kklisura.spring.spa.configuration.security.filters.AuthenticationPreserveFilter.restorePreviousAuthentication;
import static com.github.kklisura.spring.spa.configuration.security.twitter.filters.TwitterAuthorizationRequestRedirectFilter.clearOAuthTokenSessionValue;
import static com.github.kklisura.spring.spa.configuration.security.twitter.filters.TwitterAuthorizationRequestRedirectFilter.getOAuthTokenSessionValue;
import static com.github.kklisura.spring.spa.configuration.security.twitter.filters.TwitterAuthorizationRequestRedirectFilter.hasOAuthTokenSessionValue;
import static com.github.kklisura.spring.spa.controllers.external.login.ExternalLoginController.getExternalLoginBaseUrl;

import com.github.kklisura.spring.spa.configuration.security.principal.AccountPrincipal;
import com.github.kklisura.spring.spa.configuration.security.services.ExternalAccountInfoSupplier;
import com.github.kklisura.spring.spa.configuration.security.services.impl.TwitterExternalAccountInfoSupplier;
import com.github.kklisura.spring.spa.configuration.security.twitter.TwitterAuthenticationToken;
import com.github.kklisura.spring.spa.configuration.security.twitter.service.TwitterService;
import com.github.kklisura.spring.spa.controllers.external.login.ExternalLoginController;
import com.github.kklisura.spring.spa.domain.accounts.Account;
import com.github.kklisura.spring.spa.services.accounts.ExternalAccountService;
import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.social.oauth1.OAuthToken;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * Twitter callback filter either logs-in/creates account for user or shows denied page if user denied authentication.
 * It also manages linking/connecting accounts.
 *
 * @author Kenan Klisura
 */
public class TwitterCallbackFilter extends OncePerRequestFilter {

  private static final String DENIED_PARAMETER = "denied";
  private static final String OAUTH_VERIFIER_PARAMETER = "oauth_verifier";
  private static final String OAUTH_TOKEN_PARAMETER = "oauth_token";

  private static final RequestMatcher TWITTER_CALLBACK_URL = new AntPathRequestMatcher("/login/oauth1/code/twitter");

  private final TwitterService twitterService;
  private final ExternalAccountService externalAccountService;
  private final RedirectStrategy authorizationRedirectStrategy = new DefaultRedirectStrategy();

  /**
   * Instantiates a new OAuth1 twitter redirect filter.
   *
   * @param twitterService Twitter service.
   */
  public TwitterCallbackFilter(TwitterService twitterService, ExternalAccountService externalAccountService) {
    this.twitterService = twitterService;
    this.externalAccountService = externalAccountService;
  }

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {

    // /login/oauth1/code/twitter?denied=.. - when access is denied
    // /login/oauth1/code/twitter?oauth_token=...&oauth_verifier=... - when access is granted

    if (shouldRequestAuthorization(request)) {
      if (isDeniedAuthorization(request)) {
        onDeniedHandler(request, response);
      } else {
        if (verifyCallbackAttributes(request)) {
          onSuccessHandler(request, response);
        } else {
          onFailureHandler(request, response);
        }
      }

      clearOAuthTokenSessionValue(request);
    } else {
      filterChain.doFilter(request, response);
    }
  }

  private OAuthToken getAccessToken(HttpServletRequest request) {
    String oAuthVerifier = request.getParameter(OAUTH_VERIFIER_PARAMETER);
    return twitterService.getAccessToken(getOAuthTokenSessionValue(request), oAuthVerifier);
  }

  private void onSuccessHandler(HttpServletRequest request, HttpServletResponse response) throws IOException {
    final OAuthToken accessToken = getAccessToken(request);
    final SecurityContext securityContext = SecurityContextHolder.getContext();

    boolean isConnectingAccounts = hasPreviousAuthentication(request);

    if (isConnectingAccounts) {
      Authentication previousAuthentication = getPreviousAuthentication(request);
      AccountPrincipal previousPrincipal = (AccountPrincipal) previousAuthentication.getPrincipal();

      // Wrap the account supplier
      ExternalAccountInfoSupplier externalAccountInfoSupplier = new TwitterExternalAccountInfoSupplier(
          accessToken, twitterService
      );

      // Connect account with its external account.
      externalAccountService.connectAccounts(previousPrincipal.getAccount(), externalAccountInfoSupplier);

      restorePreviousAuthentication(request);
    } else {
      // Wrap the oAuth2 account supplier
      ExternalAccountInfoSupplier externalAccountInfoSupplier = new TwitterExternalAccountInfoSupplier(
          accessToken, twitterService
      );

      // Loads an account for this external login. This will either create new account or link/match with existing account
      // and will return that account.
      Account account = externalAccountService.loadAccount(externalAccountInfoSupplier);

      AccountPrincipal accountPrincipal = new AccountPrincipal(account);
      TwitterAuthenticationToken newAuthenticationToken = new TwitterAuthenticationToken(accountPrincipal);
      newAuthenticationToken.setAuthenticated(true);
      securityContext.setAuthentication(newAuthenticationToken);
    }

    clearPreviousAuthentication(request);
    authorizationRedirectStrategy
        .sendRedirect(request, response, getExternalLoginBaseUrl() + ExternalLoginController.SUCCESS_URL);
  }

  private void onFailureHandler(HttpServletRequest request, HttpServletResponse response) throws IOException {
    clearPreviousAuthentication(request);
    authorizationRedirectStrategy
        .sendRedirect(request, response, getExternalLoginBaseUrl() + ExternalLoginController.FAILURE_URL);
  }

  private void onDeniedHandler(HttpServletRequest request, HttpServletResponse response) throws IOException {
    clearPreviousAuthentication(request);
    authorizationRedirectStrategy
        .sendRedirect(request, response, getExternalLoginBaseUrl() + ExternalLoginController.DENIED_URL);
  }

  private boolean shouldRequestAuthorization(HttpServletRequest request) {
    return TWITTER_CALLBACK_URL.matches(request);
  }

  private boolean isDeniedAuthorization(HttpServletRequest request) {
    return StringUtils.isNotEmpty(request.getParameter(DENIED_PARAMETER));
  }

  private boolean verifyCallbackAttributes(HttpServletRequest request) {
    return verifyOAuthTokenAttribute(request) && verifyOAuthVerifierAttribute(request);
  }

  private boolean verifyOAuthVerifierAttribute(HttpServletRequest request) {
    String oAuthVerifierValue = request.getParameter(OAUTH_VERIFIER_PARAMETER);
    return StringUtils.isNotEmpty(oAuthVerifierValue);
  }

  private boolean verifyOAuthTokenAttribute(HttpServletRequest request) {
    String oAuthTokenValue = request.getParameter(OAUTH_TOKEN_PARAMETER);
    if (StringUtils.isNotEmpty(oAuthTokenValue)) {
      if (hasOAuthTokenSessionValue(request)) {
        final OAuthToken oAuthTokenSessionValue = getOAuthTokenSessionValue(request);
        return oAuthTokenValue.equalsIgnoreCase(oAuthTokenSessionValue.getValue());
      }
    }

    return false;
  }
}
