package com.weds.devmanages.service;

/**
 * 重试机制接口
 *
 * @author tjy
 **/
public interface DevRestart {

    boolean refreshToken(String ip);
}
