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

package com.bernardomg.example.netty.tcp.cli;

import java.io.PrintWriter;
import java.util.Objects;

import com.bernardomg.example.netty.tcp.server.TransactionListener;

/**
 * Transaction listener which will write the context of each step into a {@link PrintWriter}.
 *
 * @author Bernardo Mart&iacute;nez Garrido
 *
 */
public final class TransactionPrinterListener implements TransactionListener {

    /**
     * Port which the server will listen to.
     */
    private final Integer     port;

    /**
     * Print writer, where the messages will be sent.
     */
    private final PrintWriter writer;

    public TransactionPrinterListener(final Integer prt, final PrintWriter wrt) {
        super();

        port = Objects.requireNonNull(prt);
        writer = Objects.requireNonNull(wrt);
    }

    @Override
    public final void onRequest(final String message) {
        printHorizontalLine("RECEIVED REQUEST - START");

        if (message.isEmpty()) {
            writer.println("Received no request");
        } else {
            writer.printf("Received request: %s", message);
            writer.println();
        }

        printHorizontalLine("RECEIVED REQUEST - END");
    }

    @Override
    public final void onResponse(final String message) {
        printHorizontalLine("SENT RESPONSE - START");

        if (message.isEmpty()) {
            writer.println("Sent no response");
        } else {
            writer.printf("Sent response: %s", message);
            writer.println();
        }

        printHorizontalLine("SENT RESPONSE - END");
    }

    @Override
    public final void onStart() {
        writer.printf("Starting connection and listening to port %d", port);
        writer.println();
    }

    @Override
    public final void onStop() {
        writer.println("Stopping connection");
    }

    /**
     * Prints a horizontal line with the received header in the middle.
     *
     * @param header
     *            header to print in the line
     */
    private final void printHorizontalLine(final String header) {
        writer.println();
        writer.println(String.format("-------------------- %s --------------------", header));
        writer.println();
    }

}
