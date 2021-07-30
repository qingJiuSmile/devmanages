package com.weds.devmanages.config.signature.interceptor;


import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


/**
 * 签名拦截器配置
 *
 * @author tjy
 */
@Configuration
@ConditionalOnExpression("${weds.check: true }")
public class SignInterceptorConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 注册TestInterceptor拦截器
        InterceptorRegistration registration = registry.addInterceptor(new SignInterceptor());
        // 选择路径都被拦截
        registration.addPathPatterns("/base/**", "/config/**", "/mode/**", "/deviceRecord/**");
    }
}