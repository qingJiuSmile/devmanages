package com.weds.devmanages.entity;

import cn.afterturn.easypoi.excel.annotation.Excel;
import lombok.Data;

/**
 * 注册返回
 *
 * @author tjy
 **/
@Data
public class RegisterEntity {

    @Excel(name = "设备ip")
    private String devIp;

    @Excel(name = "分配的应用id")
    private String appId;

    @Excel(name = "分配的应用密钥")
    private String appSecret;
}
