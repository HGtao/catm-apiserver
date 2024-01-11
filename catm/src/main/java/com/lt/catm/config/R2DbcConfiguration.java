package com.lt.catm.config;

import com.lt.catm.config.converter.DemoConverter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.r2dbc.convert.R2dbcCustomConversions;
import org.springframework.data.r2dbc.dialect.MySqlDialect;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;

@Configuration
@EnableR2dbcRepositories //开启 R2dbc 仓库功能 jpa
public class R2DbcConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public R2dbcCustomConversions conversions(){
        //加入自己的转换器
       return R2dbcCustomConversions.of(MySqlDialect.INSTANCE,new DemoConverter());
    }
}
