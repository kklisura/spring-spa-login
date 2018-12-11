package com.github.kklisura.spring.spa.configuration.security.twitter.filters;

import com.github.kklisura.spring.spa.configuration.security.twitter.service.TwitterService;
import com.github.kklisura.spring.spa.configuration.security.twitter.service.TwitterService.AuthorizeUrl;
import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.social.oauth1.OAuthToken;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * Handles initial twitter login located as {@link #TWITTER_AUTHORIZATION_REQUEST}. Sends an initial OAuth1 request to
 * Twitter in order to generate a redirect URL. After successfully obtaining redirect URL it redirects the client.
 *
 * @author Kenan Klisura
 */
public class TwitterAuthorizationRequestRedirectFilter extends OncePerRequestFilter {

  public static final RequestMatcher TWITTER_AUTHORIZATION_REQUEST = new AntPathRequestMatcher(
      "/oauth1/authorization/twitter");

  private static final String OAUTH_TOKEN_VALUE = TwitterAuthorizationRequestRedirectFilter.class.getName() +
      ".OAUTH_TOKEN_VALUE";

  private final RedirectStrategy authorizationRedirectStrategy = new DefaultRedirectStrategy();
  private final TwitterService twitterService;

  /**
   * Instantiates a new OAuth1 twitter redirect filter.
   *
   * @param twitterService Twitter service.
   */
  public TwitterAuthorizationRequestRedirectFilter(TwitterService twitterService) {
    this.twitterService = twitterService;
  }

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {

    if (shouldRequestAuthorization(request)) {
      AuthorizeUrl authorizeUrl = twitterService.buildAuthorizeUrl(twitterService.getTwitterClientRegistration()
          .getCallbackUrl());

      setOAuthTokenSessionValue(request, authorizeUrl.getOAuthToken());

      authorizationRedirectStrategy.sendRedirect(request, response, authorizeUrl.getUrl());
    } else {
      filterChain.doFilter(request, response);
    }
  }

  private boolean shouldRequestAuthorization(HttpServletRequest request) {
    return TWITTER_AUTHORIZATION_REQUEST.matches(request);
  }

  private static void setOAuthTokenSessionValue(HttpServletRequest request, OAuthToken oAuthToken) {
    request.getSession().setAttribute(OAUTH_TOKEN_VALUE, oAuthToken);
  }


  /**
   * Has o auth token session value boolean.
   *
   * @param request the request
   * @return the boolean
   */
  public static boolean hasOAuthTokenSessionValue(HttpServletRequest request) {
    return request.getSession().getAttribute(OAUTH_TOKEN_VALUE) != null;
  }

  /**
   * Gets o auth token session value.
   *
   * @param request the request
   * @return the o auth token session value
   */
  public static OAuthToken getOAuthTokenSessionValue(HttpServletRequest request) {
    return (OAuthToken) request.getSession().getAttribute(OAUTH_TOKEN_VALUE);
  }

  /**
   * Clear o auth token session value.
   *
   * @param request the request
   */
  public static void clearOAuthTokenSessionValue(HttpServletRequest request) {
    request.getSession().setAttribute(OAUTH_TOKEN_VALUE, null);
  }
}
