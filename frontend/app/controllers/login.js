import Controller from '@ember/controller';
import request from 'ember-ajax/request';
import { set } from '@ember/object';
import { showOAuthWindow } from 'frontend-app/utils/auth';

export default Controller.extend({
  error: null,

  actions: {
    onLogin(username, password) {
      set(this, 'error', null);

      const options = {
        method: 'POST',
        contentType: 'application/json',
        data: { username, password }
      };

      request('/api/v1/login', options)
        .then(() => window.location.reload())
        .catch(({ payload }) => set(this, 'error', payload));

      return false;
    },

    onExternalLogin(type) {
      showOAuthWindow(type).then(() => window.location.reload());
    }
  }
});
