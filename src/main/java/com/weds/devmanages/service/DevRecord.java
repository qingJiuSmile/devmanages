package com.weds.devmanages.service;


import com.weds.devmanages.entity.record.*;


/**
 * 档案管理接口
 *
 * @author tjy
 **/
public interface DevRecord {

    /**
     * 获取档案管理信息
     *
     * @param param 请求参数
     * @return {@link DevResultEntity}
     **/
    DevArchives getArchives(DevRecordParam param);

    /**
     * 获取设备门禁规则
     *
     * @param param 请求参数
     * @return {@link DevRule}
     **/
    DevRule getRule(DevRecordParam param);

    /**
     * 获取门禁时段
     *
     * @param param 请求参数
     * @return {@link DevTimeInterval}
     **/
    DevTimeInterval getTimeInterval(DevRecordParam param);


    /**
     * 获取人脸错误信息
     *
     * @param param 请求参数
     * @return {@link DevFaceErr}
     **/
    DevFaceErr getFaceErr(DevRecordParam param);
}
