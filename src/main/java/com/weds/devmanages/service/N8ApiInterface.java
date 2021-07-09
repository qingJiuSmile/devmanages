package com.weds.devmanages.service;

import com.weds.devmanages.entity.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;

/**
 * N8设备API接口
 *
 * @author tjy
 **/
public interface N8ApiInterface {


    /**
     * 重启设备
     *
     * @param ip 设备IP
     * @return {@link boolean}
     * @author tjy
     * @date 2021/7/9
     **/
    boolean reStart(String ip);

    /**
     * 登录设备，获取token
     *
     * @param password 密码
     * @param ip       设备ip
     * @return {@link N8LoginEntity}
     * @author tjy
     **/
    N8LoginEntity login(String password, String ip);


    /**
     * 获取硬件信息
     *
     * @param ip 设备ip
     * @return {@link SysInfoEntity}
     * @author tjy
     **/
    SysInfoEntity getSysInfo(String ip);

    /**
     * 异步请求硬件信息
     *
     * @author tjy
     **/
    void asyncGetSysInfo();

    /**
     * 获取应用信息
     *
     * @param ip 设备ip
     * @return {@link AppInfoEntity}
     * @author tjy
     **/
    AppInfoEntity getAppInfo(String ip);


    /**
     * 异步获取应用信息
     *
     * @author tjy
     **/
    void asyncGetAppInfo();

    /**
     * 获取运行状态信息
     *
     * @param ip 设备ip
     **/
    RunInfoEntity getRunInfo(String ip);

    /**
     * 异步获取运行状态信息
     *
     * @author tjy
     **/
    void asyncGetRunInfo();

    /**
     * 获取磁盘空间信息
     *
     * @param ip 设备ip
     **/
    DiskInfoEntity getDiskInfo(String ip);

    /**
     * 异步获取运行状态信息
     *
     * @author tjy
     **/
    void asyncGetDiskInfo();


    /**
     * 升级设备
     *
     * @param pwd  密码
     * @param file 升级文件
     * @return {@link boolean}
     * @author tjy
     **/
    boolean deviceUpdate(String ip, String pwd, MultipartFile file);
}
