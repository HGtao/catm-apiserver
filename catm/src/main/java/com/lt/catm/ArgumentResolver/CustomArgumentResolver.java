package com.lt.catm.ArgumentResolver;

import com.lt.catm.annotation.JwtAuth;
import com.lt.catm.auth.AuthUser;
import com.lt.catm.auth.Jwt;
import com.lt.catm.common.Constants;
import com.lt.catm.exceptions.HttpException;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpCookie;
import org.springframework.web.reactive.BindingContext;
import org.springframework.web.reactive.result.method.HandlerMethodArgumentResolver;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/** 自定义参数解析器 */
public class CustomArgumentResolver implements HandlerMethodArgumentResolver {
    /** 确定此解析器是否支持特定的控制器方法参数 */
    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        JwtAuth parameterAnnotation = parameter.getParameterAnnotation(JwtAuth.class);
        return parameterAnnotation != null && AuthUser.class.equals(parameter.getParameterType());
    }

    /** 自定义参数解析器 解析参数 */
    @Override
    public Mono<Object> resolveArgument(
            MethodParameter parameter, BindingContext bindingContext, ServerWebExchange exchange) {
        // 校验JWT
        HttpCookie jwtToken =
                exchange.getRequest().getCookies().getFirst(Constants.COOKIES_JWT_NAME);
        try {
            return Mono.just(Jwt.verify(jwtToken));
        } catch (HttpException exception) {
            return Mono.error(exception);
        }
    }
}
