import request from 'ember-ajax/request';
import Route from '@ember/routing/route';

import { externalAccount } from 'frontend-app/helpers/has-external-account';
import { showOAuthWindow } from 'frontend-app/utils/auth';

export default Route.extend({
  beforeModel() {
    if (!authenticatedAccount) {
      this.transitionTo('/login');
    }
  },

  actions: {
    onLogout() {
      request('/api/v1/logout', { method: 'POST' })
        .then(() => window.location.reload());
    },

    onUnlink(type) {
      const { id: accountId } = authenticatedAccount;
      const { id } = externalAccount(type);

      request(`/api/v1/accounts/${accountId}/external-account/${id}`, { method: 'DELETE' })
        .then(() => window.location.reload());
    },

    onLink(type) {
      showOAuthWindow(type).then(() => window.location.reload());
    }
  }
});
