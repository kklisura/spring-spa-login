package com.github.kklisura.spring.spa.services.accounts.exceptions;

import com.github.kklisura.spring.spa.services.exceptions.ServiceException;

/**
 * This exception is raised when unlinking last external account from an account.
 *
 * @author Kenan Klisura
 */
public class NoPasswordSetServiceException extends ServiceException {
  public NoPasswordSetServiceException() {
    super("You have to set password first before unlinking last social account.");
  }
}
