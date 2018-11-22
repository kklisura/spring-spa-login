package com.github.kklisura.spring.spa.configuration.security.rest;

import static com.github.kklisura.spring.spa.utils.response.ResponseUtils.sendError;

import com.github.kklisura.spring.spa.controllers.errors.Error;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

/**
 * Authentication entry point that just sends FORBIDDEN error.
 *
 * The {@link #commence} method is called by the {@link org.springframework.security.web.access.ExceptionTranslationFilter}
 * when handling any <code>AccessDeniedException</code> and <code>AuthenticationException</code> thrown within the filter
 * chain.
 *
 * @author Kenan Klisura
 */
public class RestAuthenticationEntryPoint implements AuthenticationEntryPoint {

  public static final String ACCESS_DENIED = "Access denied";

  @Override
  public void commence(HttpServletRequest request, HttpServletResponse response,
      AuthenticationException authException) {
    sendError(new Error(HttpStatus.FORBIDDEN, ACCESS_DENIED), response);
  }
}
