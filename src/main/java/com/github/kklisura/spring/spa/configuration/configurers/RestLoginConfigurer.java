package com.github.kklisura.spring.spa.configuration.configurers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.kklisura.spring.spa.configuration.security.rest.JsonErrorAuthenticationFailureHandler;
import com.github.kklisura.spring.spa.configuration.security.rest.filters.RestLoginFilter;
import com.github.kklisura.spring.spa.configuration.security.rest.handlers.LoginSuccessHandler;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.HttpSecurityBuilder;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.RememberMeServices;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;

/**
 * Configures and adds a {@link RestLoginFilter} authentication filter that can parse and read
 * {@link RestLoginFilter.LoginRequest} and pass it to {@link AuthenticationManager}
 * for authentication.
 *
 * @author Kenan Klisura
 */
public class RestLoginConfigurer<B extends HttpSecurityBuilder<B>> extends
    AbstractHttpConfigurer<RestLoginConfigurer<B>, B> {

  private ObjectMapper objectMapper;

  public RestLoginConfigurer(ObjectMapper objectMapper) {
    this.objectMapper = objectMapper;
  }

  @Override
  public void configure(B builder) {
    RestLoginFilter filter = new RestLoginFilter(objectMapper);

    filter.setAuthenticationManager(builder.getSharedObject(AuthenticationManager.class));

    final AuthenticationSuccessHandler authenticationSuccessHandler = new LoginSuccessHandler();

    filter.setAuthenticationFailureHandler(new JsonErrorAuthenticationFailureHandler());
    filter.setAuthenticationSuccessHandler(authenticationSuccessHandler);

    SessionAuthenticationStrategy sessionAuthenticationStrategy = builder
        .getSharedObject(SessionAuthenticationStrategy.class);
    if (sessionAuthenticationStrategy != null) {
      filter.setSessionAuthenticationStrategy(sessionAuthenticationStrategy);
    }
    RememberMeServices rememberMeServices = builder.getSharedObject(RememberMeServices.class);
    if (rememberMeServices != null) {
      filter.setRememberMeServices(rememberMeServices);
    }

    builder.addFilterBefore(filter, UsernamePasswordAuthenticationFilter.class);
  }
}
