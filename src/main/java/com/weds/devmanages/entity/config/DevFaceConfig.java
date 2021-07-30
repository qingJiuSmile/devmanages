package com.weds.devmanages.entity.config;

import com.alibaba.fastjson.annotation.JSONField;
import com.weds.devmanages.entity.SignatureEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 设备人脸设置
 *
 * @author tjy
 **/
@Data
public class DevFaceConfig extends SignatureEntity{

    /**
     * -distance：Integer（识别距离 厘米）
     * -distanceDef : Integer（识别距离默认值 150）
     * -threshold： Float（识别阈值）
     * -thresholdDef : Float（识别阈值默认值 72.45）
     * -lightMode :  Integer（补光灯0=auto,1=open,2=close）
     * -lightModeDef : Integer（补光灯默认值 0）
     * -lightVal : Integer （补光灯亮度）
     * -lightValDef : Integer（补光灯默认值 70）
     * -cmpType : Integer（比对方式 0 - 1:N, 1 - 1:1）
     * -cmpTypeDef: Integer （对比方式默认值 0）
     * -idenMode : Integer（人脸识别模式：0-感应识别 1-手动触发识别）
     * -idenModeDef : Integer （人脸识别模式默认值 0）
     * -enableMaskVerify : Integer（ 口罩判断 1-打开 0-关闭 2-需摘口罩）
     * -enableMaskVerifyDef : Integer （ 口罩判断默认2）
     * -enableFilterHalfFace : Integer（ 过滤半脸 1-打开 0-关闭）
     * -enableFilterHalfFaceDef : Integer （ 过滤半脸默认值 0）
     * -SysFaceLiveness : Integer（活体检测 1-开启 0关闭）
     * -SysFaceLivenessDef: Integer （ 活体检测默认值 1 ）
     */

    @ApiModelProperty("识别距离 厘米 （上限300）")
    private Integer distance;

    @ApiModelProperty("识别距离默认值 150cm")
    private Integer distanceDef;

    @ApiModelProperty("识别阈值（上限95）")
    private Float threshold;

    @ApiModelProperty("识别阈值默认值 72.45")
    private Float thresholdDef;

    @ApiModelProperty("补光灯0=auto,1=open,2=close")
    private Integer lightMode;

    @ApiModelProperty("补光灯默认值 0")
    private Integer lightModeDef;

    @ApiModelProperty("补光灯亮度 (上限100)")
    private Integer lightVal;

    @ApiModelProperty("补光灯默认值 70")
    private Integer lightValDef;

    @ApiModelProperty("比对方式 0 - 1:N, 1 - 1:1")
    @JSONField(name = "cmp_type")
    private Integer cmpType;

    @ApiModelProperty("对比方式默认值 0")
    @JSONField(name = "cmp_typeDef")
    private Integer cmpTypeDef;

    @ApiModelProperty("人脸识别模式：0-感应识别 1-手动触发识别")
    @JSONField(name = "iden_mode")
    private Integer idenMode;

    @ApiModelProperty("人脸识别模式默认值 0")
    @JSONField(name = "iden_modeDef")
    private Integer idenModeDef;

    @ApiModelProperty("口罩判断 1-打开 0-关闭 2-需摘口罩")
    @JSONField(name = "enable_mask_verify")
    private Integer enableMaskVerify;

    @ApiModelProperty("口罩判断默认 2")
    @JSONField(name = "enable_mask_verifyDef")
    private Integer enableMaskVerifyDef;

    @ApiModelProperty("口罩判断 1-打开 0-关闭 2-需摘口罩")
    @JSONField(name = "enable_filter_half_face")
    private Integer enableFilterHalfFace;

    @ApiModelProperty("口罩判断默认 2")
    @JSONField(name = "enable_filter_half_faceDef")
    private Integer enableFilterHalfFaceDef;

    @ApiModelProperty("活体检测 1-开启 0关闭")
    @JSONField(name = "SysFaceLiveness")
    private Integer sysFaceLiveness;

    @ApiModelProperty("活体检测默认值 1")
    @JSONField(name = "SysFaceLivenessDef")
    private Integer sysFaceLivenessDef;
}
