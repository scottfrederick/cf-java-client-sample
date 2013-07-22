Java Cloud Foundry Client Sample
================================

This is a small Java sample application showing how to use the Cloud Foundry client runtime library to connect to and
interact with a Cloud Foundry instance.

~~~
$ git clone https://github.com/scottfrederick/cf-java-client
$ cd cf-java-client
$ ./gradlew assemble
~~~

Before running this program, you need to target and log into a Cloud Foundry service using the [`cf`](http://docs.cloudfoundry.com/docs/using/managing-apps/cf/index.html) command line tool. See the [Getting Started](http://docs.cloudfoundry.com/docs/dotcom/getting-started.html) guide for Pivotal's Cloud Foundry instance for further information. Using `cf` to target and log in will save account credentials in a file `~/.cf/tokens.yml`. This sample app uses the saved account credentials to connect to Cloud Foundry (for the sake of simplicity).

After a successful build and running `cf`, you can run the program from the created jar file:

~~~
$ java -jar build/libs/cf-java-client-1.0.jar
~~~

Without any arguments, the program will target the first Cloud Foundry service found in `~/.cf/tokens.yml`. You can pass a command-line argument to the program to target a specific Cloud Foundry service:

~~~
$ java -jar build/libs/cf-java-client-1.0.jar https://api.run.pivotal.io
~~~
