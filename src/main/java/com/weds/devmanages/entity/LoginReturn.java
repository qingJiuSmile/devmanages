package com.weds.devmanages.entity;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class LoginReturn {

    /**
     * resultCode : 0
     * sessionid : 3c441963-88ba-4b2d-876f-d6d98d72d2d6
     */

    @ApiModelProperty("0 为成功，其它值为失败")
    private Integer resultCode;

    @ApiModelProperty("会话凭证，用于后续调用识别用户身份")
    @JsonProperty("sessionid")
    private String sessionId;

    @ApiModelProperty("如果错误，则返回信息")
    private String error;


}