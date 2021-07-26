package com.weds.devmanages.service;

import com.weds.devmanages.entity.mode.DevNet;
import com.weds.devmanages.entity.mode.LinkMode;

/**
 * 设备联机设置接口
 *
 * @author tjy
 **/
public interface DevLinkMode {

    /**
     * 获取联机配置信息
     *
     * @param devIp 设备ip
     * @return {@link LinkMode}
     **/
    LinkMode getLinkMode(String devIp);

    /**
     * 设置联机配置信息
     *
     * @param devIp 设备ip
     * @param param 修改参数
     * @return {@link boolean}
     **/
    boolean setUpLinkMode(String devIp, LinkMode param);

    /**
     * 获取设备网络配置
     *
     * @param devIp 设备ip
     * @return {@link DevNet}
     **/
    DevNet getDevNet(String devIp);

    /**
     * 设置设备网络配置
     *
     * @param devIp 设置设备网络
     * @return {@link boolean}
     **/
    boolean setUpDevNet(String devIp, DevNet param);
}
