package com.github.kklisura.spring.spa.configuration.security.rest.handlers;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;

/**
 * Login success handler, writes 204 No Content on successful login.
 *
 * @author Kenan Klisura
 */
public class LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
  @Override
  protected void handle(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
    response.setStatus(HttpStatus.NO_CONTENT.value());
  }
}
