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


    /**
     * 获取人脸识别记录
     *
     * @param param 请求参数
     * @return {@link DevIdentifyEntity}
     **/
    DevIdentifyEntity getFaceIdentify(DevRecordParam param);


    /**
     * 获取外来人员记录
     *
     * @param param 请求参数
     * @return {@link DevOutsidersEntiy}
     **/
    DevOutsidersEntiy getOutsiders(DevRecordParam param);

    /**
     * 清空剩余任务
     *
     * @param param {
     *              devId,
     *              type 默认填0
     *              }
     * @return boolean
     **/
    boolean taskClearAll(DevRecordParam param);


    /**
     * 清空未上传记录
     *
     * @param param {
     *              devId,
     *              type
     *              清空未上传记录 默认填2
     *              清空人脸 默认填3
     *              清空档案 默认填2
     *              }
     * @return {@link boolean}
     * @date 2021/9/10
     **/
    boolean clearAll(DevRecordParam param);


}
