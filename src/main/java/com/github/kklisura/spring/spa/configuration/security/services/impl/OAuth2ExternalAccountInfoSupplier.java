package com.github.kklisura.spring.spa.configuration.security.services.impl;

import com.github.kklisura.spring.spa.configuration.security.oauth2.service.GithubService;
import com.github.kklisura.spring.spa.configuration.security.oauth2.service.types.UserEmail;
import com.github.kklisura.spring.spa.configuration.security.services.ExternalAccountInfoSupplier;
import com.github.kklisura.spring.spa.domain.accounts.external.ExternalAccount;
import com.github.kklisura.spring.spa.domain.accounts.external.ExternalAccount.Type;
import com.github.kklisura.spring.spa.services.accounts.AccountUsernameGeneratorService;
import com.github.kklisura.spring.spa.services.accounts.requests.AccountCreateRequest;
import java.util.List;
import java.util.Optional;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.core.user.OAuth2User;

/**
 * Wrapper around OAuth2 user as external user info supplier.
 *
 * @author Kenan Klisura
 */
public class OAuth2ExternalAccountInfoSupplier implements ExternalAccountInfoSupplier {

  private static final Logger LOGGER = LoggerFactory.getLogger(OAuth2ExternalAccountInfoSupplier.class);

  /**
   * Google claims.
   */
  private static final String GOOGLE_ATTRIBUTES_ID = "sub";
  private static final String GOOGLE_ATTRIBUTES_FIRST_NAME = "given_name";
  private static final String GOOGLE_ATTRIBUTES_LAST_NAME = "family_name";
  private static final String GOOGLE_ATTRIBUTES_EMAIL = "email";
  private static final String GOOGLE_ATTRIBUTES_AVATAR_URL = "picture";

  private static final String FACEBOOK_ATTRIBUTES_ID = "id";
  private static final String FACEBOOK_ATTRIBUTES_FULL_NAME = "name";
  private static final String FACEBOOK_ATTRIBUTES_EMAIL = "email";

  private static final String GITHUB_ATTRIBUTES_ID = "id";
  private static final String GITHUB_ATTRIBUTES_LOGIN = "login";
  private static final String GITHUB_ATTRIBUTES_NAME = "name";
  private static final String GITHUB_ATTRIBUTES_EMAIL = "email";

  private OAuth2User oAuth2User;
  private ExternalAccount.Type type;
  private OAuth2AuthorizedClient oAuth2AuthorizedClient;
  private AccountUsernameGeneratorService usernameGeneratorService;

  private GithubService githubService = new GithubService();

  /**
   * Instantiates a new O auth 2 external user info supplier.
   *
   * @param oAuth2User               OAuth2 user.
   * @param oAuth2AuthorizedClient   Authorized client.
   * @param usernameGeneratorService Username generator service.
   */
  public OAuth2ExternalAccountInfoSupplier(OAuth2User oAuth2User, OAuth2AuthorizedClient oAuth2AuthorizedClient,
      AccountUsernameGeneratorService usernameGeneratorService) {
    this.oAuth2User = oAuth2User;
    this.oAuth2AuthorizedClient = oAuth2AuthorizedClient;
    this.usernameGeneratorService = usernameGeneratorService;

    final String registrationId = oAuth2AuthorizedClient.getClientRegistration().getRegistrationId();
    this.type = Type.valueOfByRegistrationId(registrationId);
  }

  /**
   * Instantiates a new O auth 2 external user info supplier.
   *
   * @param oAuth2User               OAuth2 user.
   * @param type                     External account type.
   * @param usernameGeneratorService Username generator service.
   */
  public OAuth2ExternalAccountInfoSupplier(OAuth2User oAuth2User, Type type,
      AccountUsernameGeneratorService usernameGeneratorService) {
    this.oAuth2User = oAuth2User;
    this.usernameGeneratorService = usernameGeneratorService;
    this.type = type;
  }

  @Override
  public Type getType() {
    return type;
  }

  @Override
  public String getExternalId() {
    switch (type) {
      case GOOGLE:
        return getAttribute(oAuth2User, GOOGLE_ATTRIBUTES_ID);
      case FACEBOOK:
        return getAttribute(oAuth2User, FACEBOOK_ATTRIBUTES_ID);
      case GITHUB:
        return getAttribute(oAuth2User, GITHUB_ATTRIBUTES_ID);
      default:
        throw new UnsupportedOperationException("Unsupported external account type " + type);
    }
  }

  /**
   * Returns an external email.
   *
   * @return External email.
   */
  @Override
  public String getExternalEmail() {
    switch (type) {
      case GOOGLE:
        return getAttribute(oAuth2User, GOOGLE_ATTRIBUTES_EMAIL);
      case FACEBOOK:
        return getAttribute(oAuth2User, FACEBOOK_ATTRIBUTES_EMAIL);
      case GITHUB:
        return getGithubUserEmail(oAuth2User, oAuth2AuthorizedClient);
      default:
        throw new UnsupportedOperationException("Unsupported external account type " + type);
    }
  }

  @Override
  public AccountCreateRequest createAccountRequest() {
    switch (type) {
      case GOOGLE:
        return createGoogleAccountRequest();
      case FACEBOOK:
        return createFacebookAccountRequest();
      case GITHUB:
        return createGithubAccountRequest();
      default:
        throw new UnsupportedOperationException("Unsupported external account type " + type);
    }
  }

  /**
   * Returns a github user email.
   *
   * @param oAuth2User             OAuth user.
   * @param oAuth2AuthorizedClient Authorized client.
   * @return Github public email or one of its private email.
   */
  private String getGithubUserEmail(OAuth2User oAuth2User, OAuth2AuthorizedClient oAuth2AuthorizedClient) {
    String publicEmail = getAttribute(oAuth2User, GITHUB_ATTRIBUTES_EMAIL);

    if (StringUtils.isEmpty(publicEmail)) {
      // There is no public email for this user, we then fetch all of his emails.
      List<UserEmail> userEmails = githubService.getUserEmails(oAuth2AuthorizedClient.getAccessToken());

      // Select only primary mail from list of emails.
      Optional<UserEmail> primaryEmail = userEmails.stream()
          .filter(GithubService::isNotNoReplyEmail)
          .filter(UserEmail::isPrimary)
          .findFirst();

      if (primaryEmail.isPresent()) {
        return primaryEmail.get().getEmail();
      }

      // TODO(kklisura): What if user does not have primary email?
    }

    return publicEmail;
  }

  /**
   * Creates an account request for github users. We use email here since if there is no public email, we will fetch
   * the users emails and pick the primary one.
   *
   * @return Account create request.
   */
  private AccountCreateRequest createGithubAccountRequest() {
    AccountCreateRequest request = new AccountCreateRequest();
    request.setEmail(getExternalEmail());
    request.setUsername(generateUsername(getAttribute(oAuth2User, GITHUB_ATTRIBUTES_LOGIN)));
    request.setDisplayName(getAttribute(oAuth2User, GITHUB_ATTRIBUTES_NAME));
    return request;
  }

  /**
   * Creates a request for a new user given a facebook oauth user.
   *
   * @return Account create request.
   */
  private AccountCreateRequest createFacebookAccountRequest() {
    AccountCreateRequest request = new AccountCreateRequest();
    request.setEmail(getAttribute(oAuth2User, FACEBOOK_ATTRIBUTES_EMAIL));
    request.setDisplayName(getAttribute(oAuth2User, FACEBOOK_ATTRIBUTES_FULL_NAME));
    request.setUsername(generateUsername(getAttribute(oAuth2User, FACEBOOK_ATTRIBUTES_FULL_NAME)));
    return request;
  }

  /**
   * Creates a request for a new user given a google oauth user.
   *
   * @return Account create request.
   */
  private AccountCreateRequest createGoogleAccountRequest() {
    AccountCreateRequest request = new AccountCreateRequest();
    request.setEmail(getAttribute(oAuth2User, GOOGLE_ATTRIBUTES_EMAIL));
    request.setDisplayName(
        getAttribute(oAuth2User, GOOGLE_ATTRIBUTES_FIRST_NAME) + " " +
            getAttribute(oAuth2User, GOOGLE_ATTRIBUTES_LAST_NAME));
    request.setUsername(
        generateUsername(
            getAttribute(oAuth2User, GOOGLE_ATTRIBUTES_FIRST_NAME),
            getAttribute(oAuth2User, GOOGLE_ATTRIBUTES_LAST_NAME)
        )
    );
    return request;
  }

  /**
   * Generates a username given first and last name.
   *
   * @param firstName First name.
   * @param lastName  Last name.
   * @return Generated username.
   */
  private String generateUsername(String firstName, String lastName) {
    return generateUsername(firstName + "." + lastName);
  }

  /**
   * Generates a username given a potentialUsername.
   *
   * @param potentialUsername Potential username.
   * @return Generated username.
   */
  private String generateUsername(String potentialUsername) {
    String sanitisedUsername = potentialUsername.trim().replaceAll("\\s+", ".").toLowerCase();
    return usernameGeneratorService.generateUsername(sanitisedUsername);
  }

  private static String getAttribute(OAuth2User oAuth2User, String attribute) {
    Object attributeValue = oAuth2User.getAttributes().get(attribute);
    if (attributeValue == null) {
      LOGGER.error("Attribute {} of oAuthUser is null.", attribute);
      throw new RuntimeException("Attribute " + attribute + " of oAuthUser is null.");
    }

    return String.valueOf(attributeValue);
  }
}
