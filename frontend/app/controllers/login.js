import Controller from '@ember/controller';
import { showOAuthWindow } from 'frontend-app/utils/auth';

export default Controller.extend({
  actions: {
    onLogin(username, password) {
      console.log(username, password);
      return false;
    },

    onExternalLogin(type) {
      showOAuthWindow(type).then(() => window.location.reload());
    }
  }
});
