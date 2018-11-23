package com.github.kklisura.spring.spa.services.accounts.impl;

import com.github.kklisura.spring.spa.configuration.security.principal.AccountPrincipalUser;
import com.github.kklisura.spring.spa.domain.accounts.Account;
import com.github.kklisura.spring.spa.domain.accounts.external.ExternalAccount;
import com.github.kklisura.spring.spa.domain.accounts.external.ExternalAccount.Type;
import com.github.kklisura.spring.spa.repositories.accounts.AccountRepository;
import com.github.kklisura.spring.spa.repositories.accounts.external.ExternalAccountRepository;
import com.github.kklisura.spring.spa.services.accounts.AccountService;
import com.github.kklisura.spring.spa.services.accounts.exceptions.NoPasswordSetServiceException;
import com.github.kklisura.spring.spa.services.accounts.requests.AccountCreateRequest;
import com.github.kklisura.spring.spa.services.exceptions.EntityNotFoundServiceException;
import java.util.List;
import java.util.Optional;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Account service implementation.
 *
 * @author Kenan Klisura
 */
@Service
public class AccountServiceImpl implements AccountService {

  // TODO(kklisura): Move this to application.yml configuration.
  private static final String DEFAULT_USER_USERNAME = "admin";
  private static final String DEFAULT_USER_DISPLAY_NAME = "Kenan Klisura";
  private static final String DEFAULT_USER_EMAIL = "admin@example.com";
  private static final String DEFAULT_USER_PASSWORD = "123456";

  private static final String USERNAME_NOT_FOUND_MESSAGE = "User account not found.";

  private AccountRepository repository;
  private ExternalAccountRepository externalAccountRepository;

  private PasswordEncoder passwordEncoder;

  /**
   * Instantiates a new service.
   *
   * @param repository Account repository.
   */
  @Autowired
  public AccountServiceImpl(AccountRepository repository) {
    this.repository = repository;
  }

  /**
   * Sets external account repository.
   *
   * @param externalAccountRepository the external account repository
   */
  @Autowired
  public void setExternalAccountRepository(ExternalAccountRepository externalAccountRepository) {
    this.externalAccountRepository = externalAccountRepository;
  }

  /**
   * Sets password encoder.
   *
   * @param passwordEncoder Password encoder.
   */
  @Autowired
  public void setPasswordEncoder(PasswordEncoder passwordEncoder) {
    this.passwordEncoder = passwordEncoder;
  }

  @EventListener
  public void createDefaultUserOnStartup(ContextRefreshedEvent event) {
    // TODO(kklisura): Move this to separate class.
    createDefaultUser();
  }

  @Override
  public void createDefaultUser() {
    final Optional<Account> defaultAccount = repository.findByEmailOrUsername(DEFAULT_USER_USERNAME);

    if (!defaultAccount.isPresent()) {
      Account account = new Account();
      account.setDisplayName(DEFAULT_USER_DISPLAY_NAME);
      account.setUsername(DEFAULT_USER_USERNAME);
      account.setEmail(DEFAULT_USER_EMAIL);
      account.setPassword(passwordEncoder.encode(DEFAULT_USER_PASSWORD));
      repository.save(account);
    }
  }

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    Optional<Account> optionalAccount = repository.findByEmailOrUsername(username);

    if (optionalAccount.isPresent()) {
      Account account = optionalAccount.get();
      return new AccountPrincipalUser(account);
    }

    throw new UsernameNotFoundException(USERNAME_NOT_FOUND_MESSAGE);
  }

  /**
   * Finds an account given by external info.
   *
   * @param type External type.
   * @param externalId External id.
   * @return Account. optional
   */
  @Override
  public Optional<Account> findByExternalAccount(ExternalAccount.Type type, String externalId) {
    return repository.findByExternalAccount(type, externalId);
  }

  /**
   * Finds an account given by external info.
   *
   * @param email the email
   * @return Account. optional
   */
  @Override
  public Optional<Account> findByEmail(String email) {
    return repository.findByEmail(email);
  }

  /**
   * Create external account.
   *
   * @param account Account.
   * @param type External account type.
   * @param externalId External account id.
   */
  @Override
  public void createExternalAccount(Account account, Type type, String externalId) {
    ExternalAccount externalAccount = new ExternalAccount();

    externalAccount.setAccount(account);
    externalAccount.setExternalId(externalId);
    externalAccount.setType(type);

    externalAccountRepository.save(externalAccount);
  }

  /**
   * Creates an account given a request.
   *
   * @param request Request.
   * @return Created account.
   */
  @Override
  public Account createAccount(AccountCreateRequest request) {
    Account account = new Account();

    account.setDisplayName(request.getDisplayName());
    account.setEmail(request.getEmail());
    account.setUsername(request.getUsername());

    if (StringUtils.isNotEmpty(request.getPassword())) {
      account.setPassword(passwordEncoder.encode(request.getPassword()));
    }

    return repository.save(account);
  }

  /**
   * Returns external account for account and external account type.
   *
   * @param externalId External id.
   * @param externalAccountType External account type.
   * @return External account.
   */
  @Override
  public Optional<ExternalAccount> getExternalAccount(String externalId, Type externalAccountType) {
    return externalAccountRepository.findByExternalIdAndType(externalId, externalAccountType);
  }

  /**
   * Links an account and external account.
   *
   * @param externalAccount External account.
   * @param account Account.
   */
  @Override
  public void linkExternalAccountWithAccount(ExternalAccount externalAccount, Account account) {
    externalAccount.setAccount(account);
    externalAccountRepository.save(externalAccount);
  }

  /**
   * Generates a username given a sanitised username. Username is generated by adding a number after sanitised username.
   *
   * Output can be:
   * kenan.klisura
   * kenan.klisura.1
   * kenan.klisura.24
   *
   * @param sanitisedUsername Sanitised username.
   * @return Username. string
   */
  @Override
  public String generateUsername(String sanitisedUsername) {
    int index = 1;

    String username = sanitisedUsername;
    while (repository.existsByUsername(username)) {
      username = sanitisedUsername + "." + index;
      index++;
    }

    return username;
  }

  /**
   * Unlinks an external account from an account.
   *
   * @param id Account id.
   * @param externalAccountId External account id.
   * @throws EntityNotFoundServiceException If no account found.
   * @throws NoPasswordSetServiceException If no password set while unlinking last account.
   */
  public void unlinkExternalAccountFromAccount(Long id, Long externalAccountId)
      throws EntityNotFoundServiceException, NoPasswordSetServiceException {
    final Account account = get(id);

    final List<ExternalAccount> externalAccounts = externalAccountRepository.findAllByAccount(account);
    final Optional<ExternalAccount> externalAccount = externalAccounts.stream()
        .filter(ExternalAccount.findById(externalAccountId))
        .findFirst();

    if (externalAccount.isPresent()) {
      // Are we trying to unlink last external account?
      if (externalAccounts.size() == 1 && !account.hasPasswordSet()) {
        throw new NoPasswordSetServiceException();
      }

      externalAccountRepository.delete(externalAccount.get());
    }
  }

  /**
   * Returns entity by id.
   *
   * @param id Entity id.
   * @return Entity.
   * @throws EntityNotFoundServiceException If no entity found.
   */
  private Account get(Long id) throws EntityNotFoundServiceException {
    Optional<Account> result = repository.findById(id);
    if (!result.isPresent()) {
      throw new EntityNotFoundServiceException();
    }
    return result.get();
  }
}
