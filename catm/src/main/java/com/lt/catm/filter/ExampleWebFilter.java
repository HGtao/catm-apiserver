package com.lt.catm.filter;

import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;
import reactor.util.context.Context;

@Component
public class ExampleWebFilter implements WebFilter {
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        //将请求和响应--->exchange  封装到上下文
        return chain.filter(exchange)
                .contextWrite(Context.of(ServerWebExchange.class, exchange));
    }
}
