package com.github.kklisura.spring.spa.controllers.account;

import com.github.kklisura.spring.spa.configuration.security.principal.AccountPrincipal;
import com.github.kklisura.spring.spa.domain.accounts.Account;
import com.github.kklisura.spring.spa.services.accounts.AccountService;
import com.github.kklisura.spring.spa.services.accounts.exceptions.NoPasswordSetServiceException;
import com.github.kklisura.spring.spa.services.exceptions.EntityNotFoundServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * Accounts controller.
 *
 * @author Kenan Klisura
 */
@RestController
@RequestMapping(path = "/api/v1/accounts")
public class AccountController {

  private AccountService accountService;

  @Autowired
  public void setAccountService(AccountService accountService) {
    this.accountService = accountService;
  }

  @PreAuthorize("isAuthenticated()")
  @RequestMapping(method = RequestMethod.GET, path = "/me")
  public Account getCurrentUser(@AuthenticationPrincipal AccountPrincipal accountPrincipal)
      throws EntityNotFoundServiceException {
    return accountService.findByEmail(accountPrincipal.getAccount().getEmail())
        .orElseThrow(EntityNotFoundServiceException::new);
  }

  /**
   * Un-links external account
   *
   * @param id                Account id.
   * @param externalAccountId External account id.
   */
  @DeleteMapping("/{id}/external-account/{externalAccountId}")
  @PreAuthorize("isAuthenticated()")
  public ResponseEntity unlinkExternalAccount(@PathVariable("id") Long id,
      @PathVariable("externalAccountId") Long externalAccountId,
      @AuthenticationPrincipal AccountPrincipal accountPrincipal)
      throws EntityNotFoundServiceException, NoPasswordSetServiceException {

    if (id.equals(accountPrincipal.getAccount().getId())) {
      accountService.unlinkExternalAccountFromAccount(id, externalAccountId);
    }

    return ResponseEntity.noContent().build();
  }
}
