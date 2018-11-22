package com.github.kklisura.spring.spa.configuration.security.rest;

import static com.github.kklisura.spring.spa.utils.response.ResponseUtils.sendError;

import com.github.kklisura.spring.spa.controllers.errors.Error;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

/**
 * Authentication failure handle sends an UNAUTHORIZED error response on unsuccessful authentication.
 *
 * @author Kenan Klisura
 */
public class JsonErrorAuthenticationFailureHandler implements AuthenticationFailureHandler {

  private static final String BAD_CREDENTIALS_MESSAGE = "The username/email or password you entered is incorrect. Please try again.";

  @Override
  public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
      AuthenticationException exception) {
    sendError(new Error(HttpStatus.UNAUTHORIZED, BAD_CREDENTIALS_MESSAGE), response);
  }
}
