import Route from '@ember/routing/route';

export default Route.extend({
  beforeModel() {
    if (authenticatedAccount) {
      this.transitionTo('/');
    }
  }
});
