package com.github.kklisura.spring.spa.services.accounts;

import static com.github.kklisura.spring.spa.services.accounts.RoleTypes.ROLE_ADMIN;
import static com.github.kklisura.spring.spa.services.accounts.RoleTypes.ROLE_MEMBER;

/**
 * Roles.
 *
 * @author Kenan Klisura
 */
public enum Role {
  /**
   * Admin roles.
   */
  ADMIN(ROLE_ADMIN),
  /**
   * Member roles.
   */
  MEMBER(ROLE_MEMBER);

  private String role;

  Role(String role) {
    this.role = role;
  }

  /**
   * Gets roleName.
   *
   * @return the roleName
   */
  public String roleName() {
    return role;
  }
}
