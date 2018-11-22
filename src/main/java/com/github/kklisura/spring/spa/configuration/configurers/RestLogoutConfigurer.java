package com.github.kklisura.spring.spa.configuration.configurers;

import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.HttpSecurityBuilder;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.LogoutConfigurer;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

/**
 * REST logout configurer wraps a {@link LogoutConfigurer} and configures it with POST request on /api/v1/logout.
 *
 * {@link LogoutConfigurer} adds a {@link org.springframework.security.web.authentication.logout.LogoutFilter} to filer
 * chain which delegates to logout handlers. The handler is
 * {@link org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler}, which clears current
 * authentication from {@link org.springframework.security.core.context.SecurityContextHolder} and it also invalidates a
 * session.
 *
 * @author Kenan Klisura
 */
public class RestLogoutConfigurer<B extends HttpSecurityBuilder<B>> extends
    AbstractHttpConfigurer<RestLogoutConfigurer<B>, B> {

  private static final String LOGOUT_PATH = "/api/v1/logout";
  private static final RequestMatcher LOGOUT_REQUEST = new AntPathRequestMatcher(LOGOUT_PATH, HttpMethod.POST.name());

  private LogoutConfigurer logoutConfigurer;

  public RestLogoutConfigurer() {
    logoutConfigurer = new LogoutConfigurer<>()
        .logoutRequestMatcher(LOGOUT_REQUEST)
        .logoutSuccessHandler((request, response, authentication) -> {
          response.setStatus(HttpStatus.NO_CONTENT.value());
        });
  }

  @SuppressWarnings("unchecked")
  @Override
  public void configure(B builder) throws Exception {
    logoutConfigurer.configure(builder);
  }
}
