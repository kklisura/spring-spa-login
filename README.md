# spring-spa-login

This is an example Spring-boot based application of REST and Social login for Single Page Applications (SPA).

For Social login the following social networks are supported:
 - Facebook
 - Github
 - Google
 - Twitter
 
Features:
 - SPA friendly
 - Social login/signup via Facebook, Github, Google, Twitter
 - REST for username/password based login (`/api/v1/login`)
 - Linking/unlinking accounts
 - Separate user and external (social) accounts
 - Logout (`/api/v1/logout`)

## Configuration

Create an appropriate application on social networks to obtain Client Id and Client Secret:
 - for Facebook visit [https://developers.facebook.com/](https://developers.facebook.com/)
 - for Github visit [https://github.com/settings/developers](https://github.com/settings/developers)
 - for Google visit [https://console.developers.google.com](https://console.developers.google.com)
 - for Twitter visit [http://developer.twitter.com](http://developer.twitter.com)

Rename the `example.secrets.properties` to `.secrets.properties` and fill in the appropriate values.

## Prerequisites

The following is a list of prerequisites for building and running this project:
 - Maven 3
 - Java JDK 8
 - Java JRE 8
 - Node.js
 - npm
 - ember-cli

## Installation

Install npm dependencies (make sure you have nodejs and npm installed) for frontend by using:

```
cd frontend
npm install
```

Install ember-cli with:

```
npm install -g ember-cli
```

## How to use

Run the backend service with:

```
mvn spring-boot:run
```

Backend will start listening on port 8080 ([http://localhost:8080](http://localhost:8080)).

Run the frontend with:

```
cd frontend
ember serve --proxy http://localhost:8080
```

Frontend will start listening on port 4200 ([http://localhost:4200](http://localhost:4200)).


Login by using following credentials: `admin:123456` or by using social accounts.


## License

MIT