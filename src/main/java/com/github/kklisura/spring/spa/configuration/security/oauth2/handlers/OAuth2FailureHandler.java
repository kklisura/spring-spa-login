package com.github.kklisura.spring.spa.configuration.security.oauth2.handlers;

import static com.github.kklisura.spring.spa.controllers.external.login.ExternalLoginController.getExternalLoginBaseUrl;

import com.github.kklisura.spring.spa.controllers.external.login.ExternalLoginController;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;

/**
 * Failure handler for OAuth2 external login.
 *
 * @author Kenan Klisura
 */
public class OAuth2FailureHandler extends SimpleUrlAuthenticationFailureHandler {
  public OAuth2FailureHandler() {
    super(getExternalLoginBaseUrl() + ExternalLoginController.FAILURE_URL);
  }
}
