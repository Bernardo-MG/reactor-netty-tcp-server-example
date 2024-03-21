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

package com.bernardomg.example.netty.tcp.server;

import java.util.Objects;
import java.util.function.BiFunction;

import org.reactivestreams.Publisher;

import lombok.NonNull;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import reactor.netty.DisposableServer;
import reactor.netty.NettyInbound;
import reactor.netty.NettyOutbound;
import reactor.netty.tcp.TcpServer;

/**
 * Netty based TCP server.
 *
 * @author Bernardo Mart&iacute;nez Garrido
 *
 */
@Slf4j
public final class ReactorNettyTcpServer implements Server {

    /**
     * IO handler for the server.
     */
    private final BiFunction<NettyInbound, NettyOutbound, Publisher<Void>> handler;

    /**
     * Transaction listener. Extension hook which allows reacting to the transaction events.
     */
    private final TransactionListener                                      listener;

    /**
     * Port which the server will listen to.
     */
    private final Integer                                                  port;

    /**
     * Server for closing the connection.
     */
    private DisposableServer                                               server;

    /**
     * Wiretap flag.
     */
    @Setter
    @NonNull
    private Boolean                                                        wiretap = false;

    /**
     * Constructs a server for the given port. The transaction listener will react to events when calling the server.
     *
     * @param prt
     *            port to listen for
     * @param response
     *            response for the request
     * @param lst
     *            transaction listener
     */
    public ReactorNettyTcpServer(final Integer prt, final String response, final TransactionListener lst) {
        super();

        port = Objects.requireNonNull(prt);
        listener = Objects.requireNonNull(lst);

        handler = new ListenAndAnswerIoHandler(response, listener);
    }

    @Override
    public final void listen() {
        log.trace("Starting server listening");

        server.onDispose()
            .block();

        log.trace("Stopped server listening");
    }

    @Override
    public final void start() {
        log.trace("Starting server");

        log.debug("Binding to port {}", port);

        listener.onStart();

        server = TcpServer.create()
            // Wiretap
            .wiretap(wiretap)
            // Adds request handler
            .handle(handler)
            // Binds to port
            .port(port)
            .bindNow();

        log.trace("Started server");
    }

    @Override
    public final void stop() {
        log.trace("Stopping server");

        listener.onStop();

        server.dispose();

        log.trace("Stopped server");
    }

}
