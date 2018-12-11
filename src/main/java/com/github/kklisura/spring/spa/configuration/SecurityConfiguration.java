package com.github.kklisura.spring.spa.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.kklisura.spring.spa.configuration.configurers.OAuth2Configurer;
import com.github.kklisura.spring.spa.configuration.configurers.RestLoginConfigurer;
import com.github.kklisura.spring.spa.configuration.configurers.RestLogoutConfigurer;
import com.github.kklisura.spring.spa.configuration.configurers.TwitterLoginConfigurer;
import com.github.kklisura.spring.spa.configuration.security.rest.RestAuthenticationEntryPoint;
import com.github.kklisura.spring.spa.configuration.security.twitter.service.TwitterService;
import com.github.kklisura.spring.spa.services.accounts.AccountService;
import com.github.kklisura.spring.spa.services.accounts.ExternalAccountService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;

/**
 * Security configuration.
 *
 * @author Kenan Klisura
 */
@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

  private ObjectMapper objectMapper;
  private OAuth2AuthorizedClientService oAuth2AuthorizedClientService;

  private TwitterService twitterService;

  private AccountService accountService;
  private ExternalAccountService externalAccountService;

  @Autowired
  public void setExternalAccountService(ExternalAccountService externalAccountService) {
    this.externalAccountService = externalAccountService;
  }

  @Autowired
  public void setTwitterService(TwitterService twitterService) {
    this.twitterService = twitterService;
  }

  @Autowired(required = false)
  public void setOAuth2AuthorizedClientService(OAuth2AuthorizedClientService oAuth2AuthorizedClientService) {
    this.oAuth2AuthorizedClientService = oAuth2AuthorizedClientService;
  }

  @Autowired
  public void setAccountService(AccountService accountService) {
    this.accountService = accountService;
  }

  @Autowired
  public void setObjectMapper(ObjectMapper objectMapper) {
    this.objectMapper = objectMapper;
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Override
  protected void configure(AuthenticationManagerBuilder auth) throws Exception {
    auth.eraseCredentials(true)
        .userDetailsService(accountService)
        .passwordEncoder(passwordEncoder());
  }

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    /*
     * Since we'll be using SPA, disable the following:
     * - request cache
     * - basic auth
     * - form login
     * - CSRF
     */
    http.csrf().disable()
        .requestCache().disable()
        .httpBasic().disable()
        .formLogin().disable();

    // Make sure h2-console is accessible
    http.headers()
        .frameOptions().disable()
        .and()
        .authorizeRequests().antMatchers("/h2-console/**/*").permitAll();

    // Setup authentication entry point.
    http.exceptionHandling()
        .authenticationEntryPoint(new RestAuthenticationEntryPoint());

    // Setup REST login and logout
    http.apply(new RestLoginConfigurer<>(objectMapper))
        .and()
        .apply(new RestLogoutConfigurer<>());

    // Setup OAuth2 for Facebook, Google, Github
    if (oAuth2AuthorizedClientService != null) {
      http.apply(new OAuth2Configurer<>(externalAccountService, oAuth2AuthorizedClientService, accountService));
    }

    // Setup OAuth1 for Twitter (if the client-is is present)
    if (StringUtils.isNotEmpty(twitterService.getTwitterClientRegistration().getClientId())) {
      http.apply(new TwitterLoginConfigurer<>(twitterService, externalAccountService));
    }
  }
}
