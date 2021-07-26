package com.weds.devmanages.entity.mode;

import com.alibaba.fastjson.annotation.JSONField;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 设备网络配置
 *
 * @author tjy
 **/
@Data
public class DevNet {

    /**
     * -useDhcp： Boolean（是否为DHCP）
     * -ip : String（ip地址）
     * -mask： String（子网掩码）
     * -gateway : String（网关）
     * -dns1 : String（首选DNS）
     * -dns2 : String（备选DNS）
     */

    @ApiModelProperty("是否切换成自动获取配置")
    @JSONField(name = "isDhcp")
    private Boolean useDhcp;

    @ApiModelProperty("ip地址")
    private String ip;

    @ApiModelProperty("子网掩码")
    private String mask;

    @ApiModelProperty("网关")
    private String gateway;

    @ApiModelProperty("首选DNS")
    private String dns1;

    @ApiModelProperty("备选DNS")
    private String dns2;
}
