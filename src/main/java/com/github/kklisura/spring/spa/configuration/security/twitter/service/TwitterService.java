package com.github.kklisura.spring.spa.configuration.security.twitter.service;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.github.kklisura.spring.spa.configuration.security.twitter.config.TwitterClientRegistration;
import org.springframework.social.oauth1.OAuthToken;
import org.springframework.social.twitter.api.TwitterProfile;

/**
 * Twitter service wraps twitter API.
 *
 * @author Kenan Klisura
 */
public interface TwitterService {

  /**
   * Gets twitter client registration.
   *
   * @return Client registration.
   */
  TwitterClientRegistration getTwitterClientRegistration();

  /**
   * Build authorize url string.
   *
   * @param callbackUrl the callback url
   * @return the string
   */
  AuthorizeUrl buildAuthorizeUrl(String callbackUrl);

  /**
   * Gets access token.
   *
   * @param oAuthToken OAuth token.
   * @param oAuthVerifier OAuth verifier.
   * @return Access token.
   */
  OAuthToken getAccessToken(OAuthToken oAuthToken, String oAuthVerifier);

  /**
   * Gets user info.
   *
   * @param accessToken the access token
   * @return the user info
   */
  EnrichedTwitterProfile getUserInfo(OAuthToken accessToken);

  /**
   * The type Enriched twitter profile.
   */
  class EnrichedTwitterProfile {

    @JsonUnwrapped
    private TwitterProfile twitterProfile;

    private String email;

    /**
     * Gets twitter profile.
     *
     * @return the twitter profile
     */
    public TwitterProfile getTwitterProfile() {
      return twitterProfile;
    }

    /**
     * Sets twitter profile.
     *
     * @param twitterProfile the twitter profile
     */
    public void setTwitterProfile(TwitterProfile twitterProfile) {
      this.twitterProfile = twitterProfile;
    }

    /**
     * Gets email.
     *
     * @return the email
     */
    public String getEmail() {
      return email;
    }

    /**
     * Sets email.
     *
     * @param email the email
     */
    public void setEmail(String email) {
      this.email = email;
    }
  }

  /**
   * The type Authorize url.
   */
  class AuthorizeUrl {

    private OAuthToken oAuthToken;
    private String url;

    /**
     * Instantiates a new Authorize url.
     *
     * @param oAuthToken the o auth token
     * @param url the url
     */
    public AuthorizeUrl(OAuthToken oAuthToken, String url) {
      this.oAuthToken = oAuthToken;
      this.url = url;
    }

    /**
     * Gets auth token.
     *
     * @return the auth token
     */
    public OAuthToken getOAuthToken() {
      return oAuthToken;
    }

    /**
     * Gets url.
     *
     * @return the url
     */
    public String getUrl() {
      return url;
    }
  }
}
