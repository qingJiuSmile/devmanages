package com.weds.devmanages.service;

import com.weds.devmanages.config.log.JsonResult;

/**
 * 设备注册
 *
 * @author tjy
 **/
public interface DevRegister {

    /**
     * 注册设备
     *
     * @param devIp 设备ip
     * @param pwd   设备密码
     * @return {@link boolean}
     **/
    JsonResult<String> register(String devIp, String pwd);
}
