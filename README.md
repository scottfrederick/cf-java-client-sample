Java Cloud Foundry Client Sample
================================

This is a small Java sample application showing how to use the Cloud Foundry client runtime library to connect to and
interact with a Cloud Foundry instance.

To build the program, follow these steps:

~~~
$ git clone https://github.com/scottfrederick/cf-java-client
$ cd cf-java-client
$ ./gradlew assemble
~~~

The program can be run a few different ways. You can authenticate to Cloud Foundry with a username and password by
passing those as parameters along with the target when the program is launched:

~~~
$ java -jar build/libs/cf-java-client-1.0.jar https://api.run.pivotal.io [username] [password]
~~~

Alternatively, you can run the program using tokens saved by the [`cf`](http://docs.cloudfoundry.com/docs/using/managing-apps/cf/index.html)
command line tool instead of providing a username and password. To do this, you need to target and log into a Cloud
Foundry service using `cf`. See the [Getting Started](http://docs.cloudfoundry.com/docs/dotcom/getting-started.html)
guide for Pivotal's Cloud Foundry instance for further information. Using `cf` to target and log in will save
account credentials in a file `~/.cf/tokens.yml`.

After targetting and logging in with `cf`, you can run the program without any arguments to target the first Cloud
Foundry service found in `~/.cf/tokens.yml`:

~~~
$ java -jar build/libs/cf-java-client-1.0.jar
~~~

You can pass one command-line argument to the program to target a specific Cloud Foundry service using tokens
from  `~/.cf/tokens.yml`:

~~~
$ java -jar build/libs/cf-java-client-1.0.jar https://api.run.pivotal.io
~~~
