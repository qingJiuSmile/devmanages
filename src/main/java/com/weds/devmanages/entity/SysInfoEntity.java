package com.weds.devmanages.entity;

import lombok.Data;

import java.util.List;

/**
 * 硬件系统信息
 **/
@Data
public class SysInfoEntity {

    private String msg;

    private Integer code;

    private SysData data;

    private List<?> rows;

    private Integer total; //总数量

    @Data
    public static class SysData {

        private String devIp;

        private Integer devId;

        private String devModel;

        private String appVer;

        private String lanMac;

        private String wlanMac;

        private String sysVer;

        private String sdkVer;

        private String faceVer;

        private String serverVer;

        private String dogVer;

        private String mcuVer;

        private String androidVer;

        private String coreVer;

        private String otherVer;

        private String wsVersion;
    }

}
