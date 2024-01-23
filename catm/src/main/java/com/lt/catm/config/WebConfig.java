package com.lt.catm.config;

import com.lt.catm.resolver.CustomArgumentResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.config.DelegatingWebFluxConfiguration;
import org.springframework.web.reactive.result.method.annotation.ArgumentResolverConfigurer;

/**
 * @author zt
 */
@Configuration
public class WebConfig extends DelegatingWebFluxConfiguration {
    @Bean
    public CustomArgumentResolver customArgumentResolver() {
        return new CustomArgumentResolver();
    }

    /**
     * 注册自定义参数解析器
     */
    @Override
    protected void configureArgumentResolvers(ArgumentResolverConfigurer config) {
        config.addCustomResolver(customArgumentResolver());
    }
}
