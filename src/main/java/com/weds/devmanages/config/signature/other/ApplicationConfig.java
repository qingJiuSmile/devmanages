package com.weds.devmanages.config.signature.other;

import org.springframework.context.annotation.Bean;

//@Configuration
public class ApplicationConfig {

    @Bean
    public HttpServletRequestReplacedFilter httpServletRequestReplacedRegistration() {
        return new HttpServletRequestReplacedFilter();
    }
}
