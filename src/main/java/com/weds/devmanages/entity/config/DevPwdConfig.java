package com.weds.devmanages.entity.config;

import com.weds.devmanages.entity.SignatureEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 设置设备门禁开门密码
 *
 * @author tjy
 **/
@Data
public class DevPwdConfig extends SignatureEntity {

    @ApiModelProperty("是否开启门禁密码 1开启 0关闭")
    private Integer enSuperPwd;

    @ApiModelProperty("开启门禁密码")
    private String pwd1;

    @ApiModelProperty("开启门禁密码")
    private String pwd2;

    @ApiModelProperty("开启门禁密码")
    private String pwd3;

    @ApiModelProperty("开启门禁密码")
    private String pwd4;

    @ApiModelProperty("开启门禁密码")
    private String pwd5;

    @ApiModelProperty("开启门禁密码")
    private String pwd6;

    @ApiModelProperty("开启门禁密码")
    private String pwd7;

    @ApiModelProperty("开启门禁密码")
    private String pwd8;
}
