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

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;
import reactor.netty.NettyInbound;
import reactor.netty.NettyOutbound;

/**
 * I/O handler which sends all messages to the listener, and also answers back with a predefined message.
 *
 * @author Bernardo Mart&iacute;nez Garrido
 *
 */
@Slf4j
public final class ListenAndAnswerIoHandler implements BiFunction<NettyInbound, NettyOutbound, Publisher<Void>> {

    /**
     * Transaction listener. Reacts to events during the request.
     */
    private final TransactionListener listener;

    /**
     * Response to send after a request.
     */
    private final String              responseMessage;

    public ListenAndAnswerIoHandler(final String msg, final TransactionListener lst) {
        super();

        responseMessage = Objects.requireNonNull(msg);
        listener = Objects.requireNonNull(lst);
    }

    @Override
    public final Publisher<Void> apply(final NettyInbound request, final NettyOutbound response) {
        return request.receive()
            .asString()
            // Log request
            .doOnNext(next -> {
                // Receive request
                log.debug("Received request: {}", next);

                // Sends the request to the listener
                listener.onRequest(next);
            })
            .concatMap(next -> {
                final Publisher<String> dataStream;

                // Send response
                dataStream = Mono.just(responseMessage)
                    .flux()
                    .doOnNext(listener::onResponse);

                return response.sendString(dataStream)
                    .then();
            })
            .then();
    }

}
