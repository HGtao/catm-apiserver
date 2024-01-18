package com.lt.catm.Interceptor;

import com.lt.catm.annotation.JwtAuth;
import com.lt.catm.models.User;
import org.springframework.http.HttpCookie;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.reactive.result.method.annotation.RequestMappingHandlerMapping;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;
import reactor.util.context.Context;

import java.util.List;

/**
 * 请求拦截器
 */
@Component
public class ReqInterceptor implements WebFilter {
    private final RequestMappingHandlerMapping handlerMapping; //请求映射处理器

    public ReqInterceptor(RequestMappingHandlerMapping handlerMapping) {
        this.handlerMapping = handlerMapping;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {

        // 继续过滤器链
        return handlerMapping
                .getHandler(exchange)
                .flatMap(handler -> {
                    if (handler instanceof HandlerMethod handlerMethod) {
                        // 获取处理器方法上的注解
                        JwtAuth annotation = handlerMethod.getMethodAnnotation(JwtAuth.class);
                        if (annotation == null) {
                            //获取请求中cookie信息
                            HttpCookie jwtCookie = exchange.getRequest().getCookies().getFirst("JWT");
                            if (jwtCookie != null) {
                                String jwt = jwtCookie.getValue();
                                //调用jwt验证方法,方法返回true或false，如果是false,抛出异常
                                Mono<Boolean> aa = aa(jwt);
                               return aa.flatMap(x->{
                                    if (x){
                                        //JWT校验通过
                                        System.out.println("================================");
                                        User user = new User();
                                        user.setUsername("张三");
                                        return chain.filter(exchange).contextWrite(Context.of("user", user));
                                    }else {
                                        //JWT无效
                                        return Mono.error(new RuntimeException("JWT校验失败"));
                                    }
                                });

                            }else {
                                return Mono.error(new RuntimeException("JWT校验失败"));
                            }
                        } else {
                            //根据注解 val 确定是否需要校验
                            if (annotation.value()){
                                //需要校验
                            }else {
                                //直接放行
                            }
                            return Mono.error(new RuntimeException("没有找到注解"));
                        }
                    }
                    return chain.filter(exchange);
                })
                .onErrorResume(e -> {
                    exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                    return exchange.getResponse().setComplete();
                })
                ;
    }

    public Mono<Boolean> aa(String jwt){
        return Mono.just(true);
    }
}
