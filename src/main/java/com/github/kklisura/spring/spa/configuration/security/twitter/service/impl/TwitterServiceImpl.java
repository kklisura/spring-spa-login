package com.github.kklisura.spring.spa.configuration.security.twitter.service.impl;

import com.github.kklisura.spring.spa.configuration.security.twitter.config.TwitterClientRegistration;
import com.github.kklisura.spring.spa.configuration.security.twitter.service.TwitterService;
import java.net.URI;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.social.oauth1.AuthorizedRequestToken;
import org.springframework.social.oauth1.OAuth1Operations;
import org.springframework.social.oauth1.OAuth1Parameters;
import org.springframework.social.oauth1.OAuthToken;
import org.springframework.social.support.URIBuilder;
import org.springframework.social.twitter.api.Twitter;
import org.springframework.social.twitter.connect.TwitterServiceProvider;
import org.springframework.stereotype.Component;

/**
 * Twitter service wraps twitter API.
 *
 * @author Kenan Klisura
 */
@Component
public class TwitterServiceImpl implements TwitterService {

  private static final String TWITTER_BASE_API = "https://api.twitter.com";
  private static final String TWITTER_VERIFY_CREDENTIALS_API = "/1.1/account/verify_credentials.json";

  private static final String INCLUDE_EMAIL_PARAM = "include_email";
  private static final String TRUE = "true";

  private TwitterServiceProvider twitterServiceProvider;
  private TwitterClientRegistration twitterClientRegistration;

  /**
   * Instantiates a new Twitter service.
   *
   * @param twitterClientRegistration the twitter client registration
   */
  @Autowired
  public TwitterServiceImpl(TwitterClientRegistration twitterClientRegistration) {
    this.twitterClientRegistration = twitterClientRegistration;

    if (StringUtils.isNotEmpty(twitterClientRegistration.getClientId())) {
      twitterServiceProvider = new TwitterServiceProvider(twitterClientRegistration.getClientId(),
          twitterClientRegistration.getClientSecret());
    }
  }

  /**
   * Gets twitter client registration.
   *
   * @return Client registration.
   */
  @Override
  public TwitterClientRegistration getTwitterClientRegistration() {
    return twitterClientRegistration;
  }

  /**
   * Build authorize url string.
   *
   * @param callbackUrl the callback url
   * @return the string
   */
  @Override
  public AuthorizeUrl buildAuthorizeUrl(String callbackUrl) {
    final OAuth1Operations oAuthOperations = twitterServiceProvider.getOAuthOperations();

    OAuthToken oAuthToken = oAuthOperations.fetchRequestToken(callbackUrl, OAuth1Parameters.NONE);
    String url = oAuthOperations.buildAuthenticateUrl(oAuthToken.getValue(), OAuth1Parameters.NONE);

    return new AuthorizeUrl(oAuthToken, url);
  }

  /**
   * Gets access token.
   *
   * @param oAuthToken    OAuth token.
   * @param oAuthVerifier OAuth verifier.
   * @return Access token.
   */
  @Override
  public OAuthToken getAccessToken(OAuthToken oAuthToken, String oAuthVerifier) {
    final OAuth1Operations oAuthOperations = twitterServiceProvider.getOAuthOperations();

    final AuthorizedRequestToken authorizedRequestToken = new AuthorizedRequestToken(oAuthToken, oAuthVerifier);
    return oAuthOperations.exchangeForAccessToken(authorizedRequestToken, OAuth1Parameters.NONE);
  }

  /**
   * Gets user info.
   *
   * @param accessToken the access token
   * @return the user info
   */
  @Override
  public EnrichedTwitterProfile getUserInfo(OAuthToken accessToken) {
    Twitter twitterApi = twitterServiceProvider.getApi(accessToken.getValue(), accessToken.getSecret());

    final URI build = URIBuilder.fromUri(TWITTER_BASE_API + TWITTER_VERIFY_CREDENTIALS_API)
        .queryParam(INCLUDE_EMAIL_PARAM, TRUE)
        .build();

    return twitterApi.restOperations()
        .getForObject(build, EnrichedTwitterProfile.class);
  }
}
