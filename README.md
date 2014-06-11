Java Cloud Foundry Client Sample
================================

This is a small Java sample application showing how to use the Cloud Foundry client library to connect to and
interact with a Cloud Foundry instance.

To build the program, follow these steps:

~~~
$ git clone https://github.com/scottfrederick/cf-java-client-sample
$ cd cf-java-client-sample
$ ./gradlew assemble
~~~

The program can be run a few different ways.

## Authenticating with username and password

You can authenticate to Cloud Foundry with a username and password by passing those as parameters along with the
target when the program is launched:

~~~
$ java -jar build/libs/cf-java-client-sample-1.0.jar -t https://api.run.pivotal.io -s development -u <username> -p <password>
~~~

## Authenticating with OAuth tokens

You can authenticate to Cloud Foundry with a pair of OAuth tokens granted via an OAuth registration process. If you have
an OAuth access token and refresh token and a client ID attached to the tokens, you can pass these as parameters to the
program:

~~~
$ java -jar build/libs/cf-java-client-sample-1.0.jar -t https://api.run.pivotal.io -s development -c <client ID> -a <access token> -r <refresh token>
~~~

## Authenticating with saved tokens

You can run the program using tokens saved by the [`cf`](http://docs.cloudfoundry.com/docs/using/managing-apps/cf/index.html)
command line tool instead of providing a username and password. To do this, you need to target and log into a Cloud
Foundry service using `cf`. See the [Getting Started](http://docs.cloudfoundry.com/docs/dotcom/getting-started.html)
guide for Pivotal's Cloud Foundry instance for further information. Using `cf` to target and log in will save
account credentials in a file `~/.cf/tokens.yml`.

After targeting and logging in with `cf`, you can run the program and target a specific Cloud Foundry service using tokens
from  `~/.cf/tokens.yml`:

~~~
$ java -jar build/libs/cf-java-client-sample-1.0.jar -t https://api.run.pivotal.io -s development
~~~

