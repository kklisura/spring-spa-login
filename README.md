# spring-spa-login

This is an example Spring-boot based application of REST and Social login for Single Page Applications (SPA).

REST login is available via `/api/v1/login` API.

For Social login the following social networks are supported:
 - Facebook
 - Github
 - Google
 - Twitter 

## Configuration

TBD

## Prerequisites

The following is a list of prerequisites for building and running this project:
 - Maven 3
 - Java JDK 8
 - Java JRE 8
 - NodeJs
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

Backend will start listening on port 4200 ([http://localhost:4200](http://localhost:4200)).

## License

MIT