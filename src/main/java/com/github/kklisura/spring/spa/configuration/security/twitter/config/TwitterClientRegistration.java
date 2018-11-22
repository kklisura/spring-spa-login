package com.github.kklisura.spring.spa.configuration.security.twitter.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Twitter client registration.
 *
 * @author Kenan Klisura
 */
@Configuration
@ConfigurationProperties(prefix = "spring.security.oauth.client.registration.twitter")
public class TwitterClientRegistration {

  private String clientId;
  private String clientSecret;
  private String callbackUrl;

  /**
   * Gets callback url.
   *
   * @return the callback url
   */
  public String getCallbackUrl() {
    return callbackUrl;
  }

  /**
   * Sets callback url.
   *
   * @param callbackUrl Callback url.
   */
  public void setCallbackUrl(String callbackUrl) {
    this.callbackUrl = callbackUrl;
  }

  /**
   * Gets client id.
   *
   * @return the client id
   */
  public String getClientId() {
    return clientId;
  }

  /**
   * Sets client id.
   *
   * @param clientId the client id
   */
  public void setClientId(String clientId) {
    this.clientId = clientId;
  }

  /**
   * Gets client secret.
   *
   * @return the client secret
   */
  public String getClientSecret() {
    return clientSecret;
  }

  /**
   * Sets client secret.
   *
   * @param clientSecret the client secret
   */
  public void setClientSecret(String clientSecret) {
    this.clientSecret = clientSecret;
  }
}
