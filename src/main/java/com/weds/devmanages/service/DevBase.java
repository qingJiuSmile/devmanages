package com.weds.devmanages.service;

import com.weds.devmanages.entity.*;
import com.weds.devmanages.entity.record.RecordEntity;
import org.springframework.web.multipart.MultipartFile;


/**
 * 设备基础接口
 *
 * @author tjy
 **/
public interface DevBase {


    /**
     * 重启设备
     *
     * @param ip 设备IP
     * @return {@link boolean}
     **/
    boolean reStart(String ip);

    /**
     * 登录设备，获取token
     *
     * @param password 密码
     * @param ip       设备ip
     * @return {@link N8LoginEntity}
     **/
    N8LoginEntity login(String password, String ip);


    /**
     * 获取硬件信息
     *
     * @param ip 设备ip
     * @return {@link SysInfoEntity}
     **/
    SysInfoEntity.SysData getSysInfo(String ip);

    /**
     * 异步请求硬件信息
     **/
    void asyncGetSysInfo();

    /**
     * 获取应用信息
     *
     * @param ip 设备ip
     * @return {@link AppInfoEntity}
     **/
    AppInfoEntity.AppInfoData getAppInfo(String ip);


    /**
     * 异步获取应用信息
     **/
    void asyncGetAppInfo();

    /**
     * 获取运行状态信息
     *
     * @param ip 设备ip
     **/
    RunInfoEntity.RunInfoData getRunInfo(String ip);

    /**
     * 异步获取运行状态信息
     **/
    void asyncGetRunInfo();

    /**
     * 获取磁盘空间信息
     *
     * @param ip 设备ip
     **/
    DiskInfoEntity.DiskInfoData getDiskInfo(String ip);

    /**
     * 异步获取运行状态信息
     **/
    void asyncGetDiskInfo();


    /**
     * 升级设备
     *
     * @param file 升级文件
     * @return {@link boolean}
     **/
    boolean deviceUpdate(String ip, MultipartFile file);


    /**
     * 获取设备记录
     *
     * @param ip       设备ip
     * @param pageNum  当前页
     * @param pageSize 当前页数
     * @return {@link RecordEntity}
     **/
    RecordEntity getDevRecord(String ip, int pageNum, int pageSize);


    /**
     * 获取设备开机图片
     *
     * @param devIp 设备ip
     * @return {@link StartUpImg}
     **/
    StartUpImg getStartUpImg(String devIp);

    /**
     * 设备设备开机图片
     *
     * @param devIp     设备ip
     * @param base64Img base64图片
     * @return {@link StartUpImg}
     **/
    boolean setUpStartUpImg(String devIp, String base64Img);


    /**
     * 设备事件时间校准
     *
     * @param devIp 设备ip
     * @return {@link boolean}
     **/
    boolean timeCalibration(String devIp);

}
