package com.github.kklisura.spring.spa.configuration.security.rest.filters;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

/**
 * This filter parses the request body as JSON and passes it to
 * authenticationManager for authentication. Request JSON body is type of a LoginRequest defined
 * below.
 *
 * @author Kenan Klisura
 */
public class RestLoginFilter extends AbstractAuthenticationProcessingFilter {

  private static final String LOGIN_PATH = "/api/v1/login";

  private ObjectReader loginRequestObjectReader;

  /**
   * Instantiates a new Rest username password authentication filter.
   *
   * @param objectMapper Object mapper.
   */
  public RestLoginFilter(ObjectMapper objectMapper) {
    super(new AntPathRequestMatcher(LOGIN_PATH, HttpMethod.POST.name()));
    this.loginRequestObjectReader = objectMapper.readerFor(LoginRequest.class);
  }

  @Override
  public Authentication attemptAuthentication(HttpServletRequest request,
      HttpServletResponse response) throws AuthenticationException, IOException {

    LoginRequest loginRequest = getLoginRequest(request);

    UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken(
        loginRequest.getUsername(), loginRequest.getPassword());
    authRequest.setDetails(authenticationDetailsSource.buildDetails(request));
    return this.getAuthenticationManager().authenticate(authRequest);
  }

  private LoginRequest getLoginRequest(HttpServletRequest request) throws IOException {
    return loginRequestObjectReader.readValue(request.getInputStream());
  }

  /**
   * The type Login request.
   */
  public static class LoginRequest {

    private String username;
    private String password;

    /**
     * Gets username or email.
     *
     * @return the username or email
     */
    public String getUsername() {
      if (username == null) {
        username = "";
      }
      return username.trim();
    }

    /**
     * Sets username or email.
     *
     * @param username the username or email
     */
    public void setUsername(String username) {
      this.username = username;
    }

    /**
     * Gets password.
     *
     * @return the password
     */
    public String getPassword() {
      if (password == null) {
        password = "";
      }
      return password;
    }

    /**
     * Sets password.
     *
     * @param password the password
     */
    public void setPassword(String password) {
      this.password = password;
    }
  }
}
