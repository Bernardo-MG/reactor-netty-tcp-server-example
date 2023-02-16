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

import org.reactivestreams.Publisher;

import io.netty.util.CharsetUtil;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;
import reactor.netty.DisposableServer;
import reactor.netty.NettyInbound;
import reactor.netty.NettyOutbound;
import reactor.netty.tcp.TcpServer;

/**
 * Netty based TCP server.
 *
 * @author bernardo.martinezg
 *
 */
@Slf4j
public final class ReactorNettyTcpServer implements Server {

    /**
     * Server listener. Extension hook which allows reacting to the server events.
     */
    private final ServerListener listener;

    /**
     * Response to send after a request.
     */
    private final String         messageForClient;

    /**
     * Port which the server will listen to.
     */
    private final Integer        port;

    private DisposableServer     server;

    public ReactorNettyTcpServer(final Integer prt, final String resp, final ServerListener lst) {
        super();

        port = Objects.requireNonNull(prt);
        messageForClient = Objects.requireNonNull(resp);
        listener = Objects.requireNonNull(lst);
    }

    @Override
    public final void start() {
        log.trace("Starting server");

        listener.onStart();

        server = TcpServer.create()
            // Adds request handler
            .handle(this::handleRequest)
            // Binds to port
            .port(port)
            .bindNow();

        server.onDispose()
            .block();

        log.trace("Started server");
    }

    @Override
    public final void stop() {
        log.trace("Stopping server");

        listener.onStop();

        server.dispose();

        log.trace("Stopped server");
    }

    private final void handleError(final Throwable ex) {
        log.error(ex.getLocalizedMessage(), ex);
    }

    /**
     * Request event internal listener. Will receive any request sent by the client.
     * <p>
     * Will send the context info to the listener and send a response to the client.
     *
     * @param request
     *            request flux
     * @param response
     *            response flux
     * @return a publisher which handles the request
     */
    private final Publisher<Void> handleRequest(final NettyInbound request, final NettyOutbound response) {
        final Publisher<Void> reqPublisher;

        log.debug("Setting up request handler");

        // Publisher which sends the request to the listener
        reqPublisher = request.receive()
            .doOnNext(next -> listener.onTransaction(next.toString(CharsetUtil.UTF_8), messageForClient, true))
            .doOnError(this::handleError)
            .then();

        // Sends the response
        return response.sendString(Mono.just(messageForClient))
            .then(reqPublisher);
    }

}
