package com.github.kklisura.spring.spa.controllers.external.login;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * External login controller handles successful, unsuccessful and suggestion authentication. Successful and unsuccessful
 * (failed) authentication just notifies the parent window and closes the current window, while suggestion redirects user
 * to create user page with suggestion for full name, username and email values.
 *
 * @author Kenan Klisura
 */
@Controller
public class ExternalLoginController {

  private static final String BASE_URL_CONFIGURATION_PROPERTY = "spring.security.external-login.base-url";

  /**
   * The constant SUCCESS_URL.
   */
  public static final String SUCCESS_URL = "/external-login/success";
  /**
   * The constant FAILURE_URL.
   */
  public static final String FAILURE_URL = "/external-login/failure";
  /**
   * The constant DENIED_URL.
   */
  public static final String DENIED_URL = "/external-login/denied";
  /**
   * The constant SUGGEST_USER_INFO_URL.
   */
  public static final String SUGGEST_USER_INFO_URL = "/external-login/suggest";

  private static ExternalLoginController INSTANCE;
  private Environment environment;

  /**
   * Instantiates a new External login controller.
   *
   * @param environment the environment
   */
  @Autowired
  public ExternalLoginController(Environment environment) {
    this.environment = environment;
    INSTANCE = this;
  }

  /**
   * On successful login string.
   *
   * @return the string
   */
  @GetMapping(value = SUCCESS_URL)
  public String onSuccessfulLogin() {
    return "external-login/success";
  }

  /**
   * On failed login string.
   *
   * @return the string
   */
  @GetMapping(value = FAILURE_URL)
  public String onFailedLogin() {
    return "external-login/failure";
  }

  /**
   * On denied login string.
   *
   * @return the string
   */
  @GetMapping(value = DENIED_URL)
  public String onDeniedLogin() {
    return "external-login/denied";
  }

  /**
   * Returns an external-login handler base url. This is used for redirection purposes.
   *
   * @return Base url for external login handler.
   */
  public static String getExternalLoginBaseUrl() {
    // NOTE(kklisura): This is a hack. :( Sorry about that. Never code like this!
    // TODO(kklisura): Refactor this so this can be removed. Introduce a bean (configuration pojo).
    return INSTANCE.environment.getProperty(BASE_URL_CONFIGURATION_PROPERTY);
  }
}
