# Usage example

Once built, the JAR will be located at target/server.jar. These examples will use said path.

## Commands

### Start Server

To start the server and listen to port 8080:

```
java -jar target/server.jar start --port=8080
```

## Help

The CLI includes a help option, which shows commands:

```
java -jar target/server.jar -h
```

This extends to the commands, showing arguments and options:

```
java -jar target/server.jar start -h
```

## Debug

All the commands have a debug option, which prints logs on console:

```
java -jar target/server.jar start --port=8080 --debug
```

This includes details on all the messages sent or received.
