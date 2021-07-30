package com.weds.devmanages.entity.config;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.weds.devmanages.entity.SignatureEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalTime;

/**
 * 设备标准配置实体
 *
 * @author tjy
 **/
@Data
public class DevStdConfig extends SignatureEntity {

    @ApiModelProperty("是否开启指示灯")
    private Boolean led;

    @ApiModelProperty("是否开启指示灯默认值 true")
    private Boolean ledDef;

    @ApiModelProperty("识别方向 0=auto,1=in,2=out")
    private Integer dir;

    @ApiModelProperty("是否开启指示灯默认值 0")
    private Integer dirDef;

    @ApiModelProperty("识别间隔 秒（最大600）")
    private Integer interval;

    @ApiModelProperty("识别间隔默认值2s")
    private Integer intervalDef;

    @ApiModelProperty("开门延时 毫秒（最大6w）")
    private Integer openKeep;

    @ApiModelProperty("开门延时 3000毫秒")
    private Integer openKeepDef;

    @ApiModelProperty("音量 0 ~ 100")
    private Integer volume;

    @ApiModelProperty("音量默认值 70")
    private Integer volumeDef;

    @ApiModelProperty("设备名称")
    @JSONField(name = "site_name")
    private String siteName;

    @ApiModelProperty("语言设置 0-Chinese, 1-English")
    private Integer language;

    @ApiModelProperty("休眠开关：1-休眠, 0-不休眠")
    private Integer dormancy;

    @ApiModelProperty(value = "唤醒时间 HH:MM(当dormancy为1时)", example = "16:34")
    @JSONField(name = "wakeup_time")
    @JsonFormat(pattern = "HH:mm", timezone = "GMT+8")
    private LocalTime wakeupTime;

    @ApiModelProperty(value = "关机时间 HH:MM(当dormancy为1时)", example = "16:34")
    @JSONField(name = "shutdown_time")
    @JsonFormat(pattern = "HH:mm", timezone = "GMT+8")
    private LocalTime shutdownTime;

    @ApiModelProperty("0-标准模式 1-启用测温模式")
    @JSONField(name = "en_temp_mode")
    private Integer enTempMode;

    @ApiModelProperty("报警温度 实际温度值*100, eg：3750")
    @JSONField(name = "warn_temp")
    private Integer warnTemp;

    @ApiModelProperty("报警温度默认值 3730")
    @JSONField(name = "warn_tempDef")
    private Integer warnTempDef;

    @ApiModelProperty("联机认证模式：0-不启用, 1-启用")
    @JSONField(name = "online_auth")
    private Integer onlineAuth;

    @ApiModelProperty("联机认证模式默认值 0")
    @JSONField(name = "online_authDef")
    private Integer onlineAuthDef;

    @ApiModelProperty("火警触发方式:0-断开触发,1-闭合触发")
    @JSONField(name = "FireLinkage")
    private Integer fireLinkage;

    @ApiModelProperty("火警触发方式 默认值 1")
    @JSONField(name = "FireLinkageDef")
    private Integer fireLinkageDef;
}
