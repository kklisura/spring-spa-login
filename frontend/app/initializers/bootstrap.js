import request from 'ember-ajax/request';

export function initialize(application) {
  application.deferReadiness();

  function advanceReadiness(account) {
    window.authenticatedAccount = account;

    application.register('account:current', authenticatedAccount, { instantiate: false });
    application.inject('controller', 'authenticatedAccount', 'account:current');

    application.advanceReadiness();
  }

  request('/api/v1/accounts/me')
    .then(advanceReadiness, () => advanceReadiness(null));
}

export default {
  initialize
};
