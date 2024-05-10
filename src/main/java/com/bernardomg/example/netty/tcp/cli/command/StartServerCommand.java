/**
 * The MIT License (MIT)
 * <p>
 * Copyright (c) 2023 the original author or authors.
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * <p>
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * <p>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.bernardomg.example.netty.tcp.cli.command;

import java.io.OutputStream;
import java.io.PrintWriter;
import java.nio.charset.Charset;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.config.Configurator;

import com.bernardomg.example.netty.tcp.cli.TransactionPrinterListener;
import com.bernardomg.example.netty.tcp.cli.version.ManifestVersionProvider;
import com.bernardomg.example.netty.tcp.server.IoHandler;
import com.bernardomg.example.netty.tcp.server.ListenAndAnswerIoHandler;
import com.bernardomg.example.netty.tcp.server.ReactorNettyTcpServer;
import com.bernardomg.example.netty.tcp.server.Server;
import com.bernardomg.example.netty.tcp.server.SinkIoHandler;
import com.bernardomg.example.netty.tcp.server.TransactionListener;

import picocli.CommandLine.Command;
import picocli.CommandLine.Help;
import picocli.CommandLine.Model.CommandSpec;
import picocli.CommandLine.Option;
import picocli.CommandLine.Spec;

/**
 * Start server. This creates a server which listens for requests, if the response is defined it will also answer them.
 *
 * @author Bernardo Mart&iacute;nez Garrido
 *
 */
@Command(name = "start", description = "Starts a TCP server", mixinStandardHelpOptions = true,
        versionProvider = ManifestVersionProvider.class)
public final class StartServerCommand implements Runnable {

    /**
     * Debug flag. Shows debug logs.
     */
    @Option(names = { "--debug" }, paramLabel = "flag", description = "Enable debug logs.", defaultValue = "false")
    private boolean     debug;

    /**
     * Port to listen.
     */
    @Option(names = { "-p", "--port" }, paramLabel = "port", description = "Port to listen.", required = true)
    private Integer     port;

    /**
     * Response to return.
     */
    @Option(names = { "-r", "--response" }, paramLabel = "response",
            description = "Response to send back after receiving a request.")
    private String      response;

    /**
     * Command specification. Used to get the line output.
     */
    @Spec
    private CommandSpec spec;

    /**
     * Verbose mode. If active prints info into the console. Active by default.
     */
    @Option(names = { "--verbose" }, paramLabel = "flag", description = "Print information to console.",
            defaultValue = "true", showDefaultValue = Help.Visibility.ALWAYS)
    private boolean     verbose;

    /**
     * Default constructor.
     */
    public StartServerCommand() {
        super();
    }

    @Override
    public final void run() {
        final PrintWriter         writer;
        final Server              server;
        final TransactionListener listener;
        final IoHandler           handler;

        if (debug) {
            activateDebugLog();
        }

        if (verbose) {
            // Prints to console
            writer = spec.commandLine()
                .getOut();
        } else {
            // Prints nothing
            writer = new PrintWriter(OutputStream.nullOutputStream(), false, Charset.defaultCharset());
        }

        // Create server
        listener = new TransactionPrinterListener(port, writer);
        if (response == null) {
            // Missing response, will just sink requests
            handler = new SinkIoHandler(listener);
        } else {
            handler = new ListenAndAnswerIoHandler(response, listener);
        }
        server = new ReactorNettyTcpServer(port, listener, handler, debug);

        // Start server
        server.start();
        server.listen();

        // Stop server
        server.stop();

        // Close writer
        writer.close();
    }

    /**
     * Activates debug logs for the application.
     */
    private final void activateDebugLog() {
        Configurator.setLevel("com.bernardomg.example", Level.DEBUG);
        Configurator.setLevel("reactor.netty.tcp", Level.DEBUG);
    }

}
