package com.weds.devmanages.service;

/**
 * 重试机制接口
 *
 * @author tjy
 **/
public interface RestartInterface {


    /**
     * 根据设备IP添加重试任务
     *
     * @param ip 设备ip
     * @return {@link Runnable}
     * @author tjy
     * @date 2021/7/8
     **/
    Runnable restart(String ip);
}
