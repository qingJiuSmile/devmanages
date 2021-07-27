package com.weds.devmanages.service.sign;

import java.lang.annotation.Documented;

import java.lang.annotation.Retention;

import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({FIELD})

@Retention(RUNTIME)

@Documented

/**
 *  针对于字段的签名
 * @author tjy
 **/
public @interface SignatureField {

    //签名顺序
    int order() default 0;

    //字段name自定义值
    String customName() default "";

    //字段value自定义值
    String customValue() default "";

}