package com.weds.devmanages.entity;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * 硬件系统信息
 **/
@Data
public class SysInfoEntity {

    private List<?> rows;

    private Integer total; //总数量

    @Data
    public static class SysData {

        @ApiModelProperty("分辨率高度")
        private Integer scrHeight;

        @ApiModelProperty("分辨率宽度")
        private Integer scrWidth;

        @ApiModelProperty("设备IP")
        private String devIp;

        @ApiModelProperty("设备ID")
        private Integer devId;

        @ApiModelProperty("设备型号")
        private String devModel;

        @ApiModelProperty("应用版本")
        private String appVer;

        @ApiModelProperty("以太网MAC")
        private String lanMac;

        @ApiModelProperty("无线网MAC")
        private String wlanMac;

        @ApiModelProperty("平台版本")
        private String sysVer;

        @ApiModelProperty("SDK版本")
        private String sdkVer;

        @ApiModelProperty("人脸版本")
        private String faceVer;

        @ApiModelProperty("服务版本")
        private String serverVer;

        @ApiModelProperty("看门狗版本")
        private String dogVer;

        @ApiModelProperty("单片机版本")
        private String mcuVer;

        @ApiModelProperty("Android版本")
        private String androidVer;

        @ApiModelProperty("内核版本")
        private String coreVer;

        @ApiModelProperty("其它版本")
        private String otherVer;

        @ApiModelProperty("web服务版本")
        private String wsVersion;
    }

}
