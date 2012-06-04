# NoTime

A timesheet application written in Java using the Play 2 framework. Currently running on version 2.0.1.

## Installation

### Local

- Install Play 2, version 2.0.1 (http://www.playframework.org/documentation/2.0.1/Installing)
- Checkout this repository and launch the Play 2 development console:

```bash
$ play
```
More information about the Play console: http://www.playframework.org/documentation/2.0.1/PlayConsole

By default you can login with username admin and password admin.

### Heroku

- Create a Heroku account and install the client (https://devcenter.heroku.com/articles/quickstart)
- Checkout this repository
- Login to Heroku from the command line:

```bash
$ heroku login
```

- Create a new Heroku application using the 'cedar' stack:

```bash
$ heroku create -s cedar
```

- Push repository to Heroku

```bash
$ git push heroku master
```

- Open the application

```bash
$ heroku open
```

By default you can login with username admin and password admin.

## Issues tracker

Report issues at https://github.com/lunatech-labs/lunatech-notime/issues.