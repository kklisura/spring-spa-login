package com.github.kklisura.spring.spa.configuration.security.oauth2.service;

import com.github.kklisura.spring.spa.configuration.security.oauth2.service.types.UserEmail;
import com.nimbusds.oauth2.sdk.http.HTTPRequest;
import com.nimbusds.oauth2.sdk.http.HTTPResponse;
import com.nimbusds.oauth2.sdk.token.BearerAccessToken;
import com.nimbusds.openid.connect.sdk.UserInfoRequest;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.GenericHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;

/**
 * Github service abstracts github API.
 *
 * @author Kenan Klisura
 */
public class GithubService {

  public static final String NO_REPLY_EMAIL = "@users.noreply.github.com";
  private static final String GITHUB_USER_MAILS = "https://api.github.com/user/emails";
  private static final String INVALID_RESPONSE = "invalid_response";
  private static final int CONNECTION_TIMEOUT = 30000;
  private static final int READ_TIMEOUT = 30000;
  private final GenericHttpMessageConverter genericHttpMessageConverter = new MappingJackson2HttpMessageConverter();

  /**
   * Gets list of user emails.
   *
   * @param oauth2AccessToken Access token.
   * @return List of user emails.
   * @throws OAuth2AuthenticationException If exception occurs for any reason.
   */
  public List<UserEmail> getUserEmails(OAuth2AccessToken oauth2AccessToken) throws OAuth2AuthenticationException {
    HTTPResponse httpResponse = sendRequest(oauth2AccessToken.getTokenValue(), GITHUB_USER_MAILS);
    if (httpResponse.getStatusCode() == HTTPResponse.SC_OK) {
      ParameterizedTypeReference<List<UserEmail>> typeRef = new ParameterizedTypeReference<List<UserEmail>>() {
      };
      return parseJsonHttpResponse(httpResponse, typeRef.getType(), UserEmail.class);
    }

    OAuth2Error oauth2Error = new OAuth2Error(INVALID_RESPONSE, GITHUB_USER_MAILS +
        " returned " + httpResponse.getStatusCode() + " " + httpResponse.getStatusMessage(), null);
    throw new OAuth2AuthenticationException(oauth2Error, oauth2Error.toString());
  }

  /**
   * Sends the request to specified uri given a access token.
   *
   * @param oauth2AccessToken Access token.
   * @param uri               Uri.
   * @return HTTP Response.
   */
  private HTTPResponse sendRequest(String oauth2AccessToken, String uri) {
    URI requestUri = URI.create(uri);
    BearerAccessToken accessToken = new BearerAccessToken(oauth2AccessToken);

    UserInfoRequest request = new UserInfoRequest(requestUri, accessToken);
    HTTPRequest httpRequest = request.toHTTPRequest();
    httpRequest.setAccept(MediaType.APPLICATION_JSON_VALUE);
    httpRequest.setConnectTimeout(CONNECTION_TIMEOUT);
    httpRequest.setReadTimeout(READ_TIMEOUT);

    try {
      return httpRequest.send();
    } catch (IOException ex) {
      throw new AuthenticationServiceException(
          "An error occurred while sending the request to " + uri + ": " + ex.getMessage(), ex);
    }
  }

  @SuppressWarnings("unchecked")
  private <T> T parseJsonHttpResponse(HTTPResponse httpResponse, Class<T> clazz) {
    try {
      return (T) this.genericHttpMessageConverter.read(clazz, new HttpInputResponse(httpResponse));
    } catch (IOException e) {
      OAuth2Error oauth2Error = new OAuth2Error(INVALID_RESPONSE,
          "An error occurred reading response: " + e.getMessage(), null);
      throw new OAuth2AuthenticationException(oauth2Error, oauth2Error.toString(), e);
    }
  }

  @SuppressWarnings("unchecked")
  private <R, T> R parseJsonHttpResponse(HTTPResponse httpResponse, Type type, Class<T> clazz) {
    try {
      return (R) this.genericHttpMessageConverter.read(type, clazz, new HttpInputResponse(httpResponse));
    } catch (IOException e) {
      OAuth2Error oauth2Error = new OAuth2Error(INVALID_RESPONSE,
          "An error occurred reading response: " + e.getMessage(), null);
      throw new OAuth2AuthenticationException(oauth2Error, oauth2Error.toString(), e);
    }
  }

  /**
   * Returns true if userEmail is no NO_REPLY_EMAIL and is not empty.
   *
   * @param userEmail User email.
   * @return True if user email is no NO_REPLY_EMAIL.
   */
  public static boolean isNotNoReplyEmail(UserEmail userEmail) {
    if (StringUtils.isEmpty(userEmail.getEmail())) {
      return false;
    }

    return !userEmail.getEmail().toLowerCase().contains(NO_REPLY_EMAIL);
  }

  private static class HttpInputResponse implements HttpInputMessage {

    private HTTPResponse httpResponse;
    private HttpHeaders httpHeaders;

    /**
     * Instantiates a new Http input response.
     *
     * @param httpResponse the http response
     */
    HttpInputResponse(HTTPResponse httpResponse) {
      this.httpResponse = httpResponse;
      this.httpHeaders = new HttpHeaders();
      this.httpHeaders.setAll(httpResponse.getHeaders());
    }

    @Override
    public InputStream getBody() {
      return new ByteArrayInputStream(
          this.httpResponse.getContent().getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public HttpHeaders getHeaders() {
      return httpHeaders;
    }
  }
}
