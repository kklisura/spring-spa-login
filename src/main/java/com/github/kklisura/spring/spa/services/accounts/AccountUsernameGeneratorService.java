package com.github.kklisura.spring.spa.services.accounts;

/**
 * Username generator service.
 *
 * @author Kenan Klisura
 */
public interface AccountUsernameGeneratorService {

  /**
   * Generates a username.
   *
   * @param sanitisedUsername Sanitised username.
   * @return Username.
   */
  String generateUsername(String sanitisedUsername);
}
