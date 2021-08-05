package com.weds.devmanages.service.sign;

import java.lang.annotation.Documented;

import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;

import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;

import static java.lang.annotation.ElementType.TYPE;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * 签名注解
 *
 * @author tjy
 **/
@Target({TYPE, METHOD})
@Retention(RUNTIME)
@Documented
@Inherited
public @interface Signature {
}