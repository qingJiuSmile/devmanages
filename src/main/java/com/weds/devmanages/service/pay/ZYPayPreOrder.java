package com.weds.devmanages.service.pay;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonFilter;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 *
 **/
@Data
public class ZYPayPreOrder {

    @JSONField(name = "tran_no")
    @ApiModelProperty("统一支付订单号")
    private String tranNo;

    @ApiModelProperty("0表示成功，其它表示失败")
    private Integer statusCode;

    @ApiModelProperty("0表示成功，其它表示失败")
    @JSONField(name = "ret_code")
    private Integer retCode;

    @JSONField(name = "cashier_url")
    @ApiModelProperty("收银台地址")
    private String cashierUrl;

    @ApiModelProperty("失败时返回提示信息")
    private String message;
}
