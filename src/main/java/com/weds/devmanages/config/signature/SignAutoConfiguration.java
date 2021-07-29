package com.weds.devmanages.config.signature;

import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 签名校验配置
 *
 * @author tjy
 **/
@Configuration
@ConditionalOnExpression("${weds.check: true }")
public class SignAutoConfiguration {

    @Bean
    public SignStreamFilter initSignFilter() {
        return new SignStreamFilter();
    }

    @Bean
    public FilterRegistrationBean webAuthFilterRegistration() {
        FilterRegistrationBean registration = new FilterRegistrationBean();
        registration.setFilter(initSignFilter());
        registration.setName("signFilter");
        registration.addUrlPatterns("/base/*", "/config/*", "/mode/*", "/deviceRecord/*", "/example/*");
        registration.setOrder(0);
        return registration;
    }
}
