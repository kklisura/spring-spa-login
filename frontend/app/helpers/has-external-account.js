import { helper } from '@ember/component/helper';

export function externalAccount(type) {
  const result = (authenticatedAccount.externalAccounts || [])
    .find(account => type === account.type);

  return result;
}

export function hasExternalAccount(params) {
  if (!authenticatedAccount) {
    return false;
  }

  return !!externalAccount(params[0]);
}

export default helper(hasExternalAccount);
