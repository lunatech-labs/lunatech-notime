# NoTime

A timesheet application written in Java using the Play 2 framework. Currently running on version 2.0.1.

## Installion

### Local

1. Install Play 2, version 2.0.1 (http://www.playframework.org/documentation/2.0.1/Installing)
2. Checkout this repository and launch the Play 2 development console:

```bash
$ play
```
More information about the Play console: http://www.playframework.org/documentation/2.0.1/PlayConsole

### Heroku

1. Create a Heroku account and install the client (https://devcenter.heroku.com/articles/quickstart)
2. Checkout this repository
3. Login to Heroku from the command line:

```bash
$ heroku login
```

4. Create a new Heroku application using the 'cedar' stack:

```bash
$ heroku create -s cedar
```

5. Push repository to Heroku

```bash
$ git push heroku master
```

6. Open the application

```bash
$ heroku open
```

## Issues tracker

Report issues at https://github.com/lunatech-labs/lunatech-notime/issues.