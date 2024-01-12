package com.lt.catm.config;

import org.springframework.boot.autoconfigure.security.reactive.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
public class AppSecurityConfiguration {
    @Bean
    SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        http.authorizeExchange(authorize -> {
            //指定路径
//            authorize.pathMatchers("/xxx");

            //允许所有人访问静态资源
            authorize.matchers(PathRequest.toStaticResources().atCommonLocations()).permitAll();

            //其他所有请求都需要认证
            authorize.anyExchange().authenticated();


            //安全控制 禁用
            http.csrf(ServerHttpSecurity.CsrfSpec::disable);
        });

        return http.build();
    }
}
