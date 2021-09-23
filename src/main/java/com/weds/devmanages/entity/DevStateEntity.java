package com.weds.devmanages.entity;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 设备状态信息实体
 *
 * @author tjy
 **/
@Data
@Accessors(chain = true)
public class DevStateEntity {

    @ApiModelProperty("设备ip")
    private String devIp;

    @ApiModelProperty("设备状态（1在线,2离线）")
    private Integer devState;

    @ApiModelProperty("设备状态（文字描述）")
    private String devStateStr;

    @ApiModelProperty("设备状态记录时间(正常)")
    private String successTime;

    @ApiModelProperty("设备状态记录时间(错误)")
    private String errorTime;

    @ApiModelProperty("连接错误描述")
    private String errorMsg;


    public String getDevStateStr() {
        return devState == 1 ? "在线" : "离线";
    }

    public void setDevStateStr(String devStateStr) {
        this.devStateStr = devStateStr;
    }
}
