package com.weds.devmanages.config.signature.aop;

import org.springframework.context.annotation.Bean;

//@Configuration
public class ApplicationConfig {

    @Bean
    public HttpServletRequestReplacedFilter httpServletRequestReplacedRegistration() {
        return new HttpServletRequestReplacedFilter();
    }
}
