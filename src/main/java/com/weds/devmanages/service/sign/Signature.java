package com.weds.devmanages.service.sign;

import java.lang.annotation.Documented;

import java.lang.annotation.Retention;

import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;

import static java.lang.annotation.ElementType.TYPE;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({TYPE, METHOD})

@Retention(RUNTIME)

@Documented

/**
 *  签名注解
 * @author tjy
 **/
public @interface Signature {

    // 按照order值排序
    String ORDER_SORT = "ORDER_SORT";

    // 字典序排序
    String ALPHA_SORT = "ALPHA_SORT";

    // 允许重复请求
    boolean resubmit() default true;

    String sort() default Signature.ALPHA_SORT;

}