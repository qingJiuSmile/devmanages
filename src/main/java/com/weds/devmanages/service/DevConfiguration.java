package com.weds.devmanages.service;

import com.weds.devmanages.entity.config.DevFaceConfig;
import com.weds.devmanages.entity.config.DevPwdConfig;
import com.weds.devmanages.entity.config.DevStdConfig;
import com.weds.devmanages.entity.config.DevViewConfig;

/**
 * 配置设备接口
 *
 * @author tjy
 **/
public interface DevConfiguration {

    /**
     * 获取设备标准配置参数
     *
     * @param devIp 设备ip
     * @return {@link DevStdConfig}
     **/
    DevStdConfig getDevStdConfig(String devIp);

    /**
     * 配置设备
     *
     * @param param 配置参数
     * @param devIp 设备ip
     * @return {@link boolean}
     **/
    boolean configureTheDevice(String devIp, DevStdConfig param);

    /**
     * 获取设备人脸配置信息
     *
     * @param devIp 设备IP
     * @return {@link DevFaceConfig}
     **/
    DevFaceConfig getDevFaceConfig(String devIp);

    /**
     * 设置设备人脸信息
     *
     * @param devIp 设备ip
     * @param param 人脸配置参数
     * @return {@link boolean}
     **/
    boolean configureDeviceFace(String devIp, DevFaceConfig param);

    /**
     * 获取设备界面信息
     *
     * @param devIp 设备ip
     * @return {@link DevViewConfig}
     **/
    DevViewConfig getDevViewConfig(String devIp);

    /**
     * 设置设备信息界面
     *
     * @param devIp 设备ip
     * @param param 设备界面信息配置
     * @return {@link boolean}
     **/
    boolean configureDeviceView(String devIp, DevViewConfig param);

    /**
     * 获取设备门禁开门密码
     *
     * @param devIp 设备ip
     * @return {@link DevViewConfig}
     **/
    DevPwdConfig getDevPwdConfig(String devIp);

    /**
     * 获取设备门禁开门密码
     *
     * @param devIp 设备ip
     * @param param 设备界面信息配置
     * @return {@link boolean}
     **/
    boolean configureDevicePwd(String devIp, DevPwdConfig param);
}
