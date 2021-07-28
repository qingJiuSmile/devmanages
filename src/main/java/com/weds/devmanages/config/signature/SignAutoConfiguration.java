package com.weds.devmanages.config.signature;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 签名校验配置
 *
 * @author tjy
 **/
@Configuration
public class SignAutoConfiguration {

    @Bean
    public SignStreamFilter initSignFilter() {
        return new SignStreamFilter();
    }
}
