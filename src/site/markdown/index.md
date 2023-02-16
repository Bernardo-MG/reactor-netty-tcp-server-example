# Netty TCP Client Example

This is a basic Maven-based Java project created with the use of the [Library Maven Archetype](https://github.com/Bernardo-MG/library-maven-archetype). It will ease the development of new libraries, setting it up for CI through the use of free services: [Github](https://github.com/), [Github Workflow](https://docs.github.com/en/actions/configuring-and-managing-workflows), [Github Packages](https://github.com/features/packages) and [OSS Sonatype](https://oss.sonatype.org/).

To use the project first package it:

```
mvn clean package
```

The JAR will be a runnable Java file. It can be executed like this:

```
java -jar target/client.jar message 127.0.0.1 8080 Hello
```

To show other commands:

```
java -jar target/client.jar -h
```

You can use this project along the [Netty TCP Server Example](https://github.com/Bernardo-MG/netty-tcp-server-example) to experiment with TCP communications between a client and a server.
