const POPUP_WINDOW_WIDTH = 800;
const POPUP_WINDOW_HEIGHT = 600;
const POPUP_WINDOW_TITLE = 'ConnectWithOAuth'; // should not include space for IE

function registerOneTimeHandler(name, callback) {
  window[name] = function() {
    callback();
    window[name] = null;
  };
}

function getPopupWindowOptions() {
  // Fixes dual-screen position                         Most browsers      Firefox
  const dualScreenLeft = window.screenLeft != undefined ? window.screenLeft : window.screenX;
  const dualScreenTop = window.screenTop != undefined ? window.screenTop : window.screenY;

  const width = window.innerWidth
    ? window.innerWidth
    : document.documentElement.clientWidth
      ? document.documentElement.clientWidth
      : screen.width;
  const height = window.innerHeight
    ? window.innerHeight
    : document.documentElement.clientHeight
      ? document.documentElement.clientHeight
      : screen.height;

  const left = width / 2 - POPUP_WINDOW_WIDTH / 2 + dualScreenLeft;
  const top = height / 2 - POPUP_WINDOW_HEIGHT / 2 + dualScreenTop;

  return `status=0,scrollbars=yes,width=${POPUP_WINDOW_WIDTH},height=${POPUP_WINDOW_HEIGHT},top=${top},left=${left}`;
}

function waitForWindowClose(newWindow, callback) {
  let interval;
  interval = setInterval(function() {
    if (newWindow.closed) {
      clearInterval(interval);
      callback();
    }
  }, 1000);
}

export function showOAuthWindow(oAuthType, callback) {
  let path = `/oauth2/authorization/${oAuthType}`;
  if (oAuthType === 'twitter') {
    path = `/oauth1/authorization/${oAuthType}`;
  }

  return new Promise(function(resolve, reject) {
    registerOneTimeHandler('_EXTERNAL_LOGIN_onExternalLoginSuccess', () => resolve());
    registerOneTimeHandler('_EXTERNAL_LOGIN_onExternalLoginDenied', () => reject());

    let newWindow = open(path, POPUP_WINDOW_TITLE, getPopupWindowOptions());
    if (window.focus) {
      newWindow.focus();
    }

    waitForWindowClose(newWindow, () => reject());
  });
}
