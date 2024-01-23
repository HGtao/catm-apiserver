package com.lt.catm.utils;

import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

public class ReactiveRequestContextHolder {
    private static final Class<ServerWebExchange> CONTEXT_KEY = ServerWebExchange.class;

    /**
     * 拿到请求中的上下文
     */
    public static Mono<ServerWebExchange> getExchange() {
        return Mono.deferContextual(contextView -> Mono.justOrEmpty(contextView.get(CONTEXT_KEY)));
    }
}
