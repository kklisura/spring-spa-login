package com.github.kklisura.spring.spa.configuration.security.filters;

import com.github.kklisura.spring.spa.configuration.security.principal.AccountPrincipal;
import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestRedirectFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * Preserves the authentication if present and is not anonymous as session attribute with name
 * {@link #PREVIOUS_AUTHENTICATION_ATTRIBUTE_NAME}.
 *
 * @author Kenan Klisura
 */
public class AuthenticationPreserveFilter extends OncePerRequestFilter {

  private static final String PREVIOUS_AUTHENTICATION_ATTRIBUTE_NAME =
      AuthenticationPreserveFilter.class.getName() + ".PREVIOUS_AUTHENTICATION_ATTRIBUTE_NAME";

  private static final RequestMatcher FILTER_REQUEST_MATCHER = new AntPathRequestMatcher(
      OAuth2AuthorizationRequestRedirectFilter.DEFAULT_AUTHORIZATION_REQUEST_BASE_URI + "/*");

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
      FilterChain filterChain) throws ServletException, IOException {

    if (shouldPreserveAuthentication(request)) {
      SecurityContext context = SecurityContextHolder.getContext();
      Authentication currentAuthentication = context.getAuthentication();
      if (currentAuthentication != null && !(currentAuthentication instanceof AnonymousAuthenticationToken)) {
        // Save the previous authentication object, if any.
        request.getSession().setAttribute(PREVIOUS_AUTHENTICATION_ATTRIBUTE_NAME, currentAuthentication);
      }
    }

    filterChain.doFilter(request, response);
  }

  private boolean shouldPreserveAuthentication(HttpServletRequest request) {
    return FILTER_REQUEST_MATCHER.matches(request) && !hasPreviousAuthentication(request);
  }

  /**
   * Checks if session contains previous authentication.
   *
   * @param servletRequest Request.
   * @return True if session contains previous authentication.
   */
  public static boolean hasPreviousAuthentication(ServletRequest servletRequest) {
    HttpServletRequest request = (HttpServletRequest) servletRequest;
    Object sessionAttribute = request.getSession().getAttribute(PREVIOUS_AUTHENTICATION_ATTRIBUTE_NAME);

    if (sessionAttribute != null && sessionAttribute instanceof Authentication) {
      Authentication authentication = (Authentication) sessionAttribute;

      if (authentication.getPrincipal() != null && authentication.getPrincipal() instanceof AccountPrincipal) {
        return true;
      }
    }

    return false;
  }

  /**
   * Gets the previous authentication.
   *
   * @param servletRequest Request.
   * @return Previous authentication
   */
  public static Authentication getPreviousAuthentication(ServletRequest servletRequest) {
    HttpServletRequest request = (HttpServletRequest) servletRequest;
    return (Authentication) request.getSession().getAttribute(PREVIOUS_AUTHENTICATION_ATTRIBUTE_NAME);
  }

  /**
   * Clear the previous authentication.
   *
   * @param servletRequest Request.
   */
  public static void clearPreviousAuthentication(ServletRequest servletRequest) {
    HttpServletRequest request = (HttpServletRequest) servletRequest;
    request.getSession().removeAttribute(PREVIOUS_AUTHENTICATION_ATTRIBUTE_NAME);
  }

  /**
   * Restore a previous authentication if its available, ie. sets the authentication for current session and
   * clears the previous authentication.
   *
   * @param servletRequest Servlet request.
   */
  public static void restorePreviousAuthentication(ServletRequest servletRequest) {
    if (hasPreviousAuthentication(servletRequest)) {
      final Authentication previousAuthentication = getPreviousAuthentication(servletRequest);
      SecurityContext securityContext = SecurityContextHolder.getContext();
      securityContext.setAuthentication(previousAuthentication);

      clearPreviousAuthentication(servletRequest);
    }
  }
}
