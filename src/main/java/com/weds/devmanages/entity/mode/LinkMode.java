package com.weds.devmanages.entity.mode;

import com.alibaba.fastjson.annotation.JSONField;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 连接设置设置
 *
 * @author tjy
 **/

@Data
public class LinkMode {

    @ApiModelProperty("模式:0=sdk,1=http,2=mqtt,3=websocket")
    private Integer linkMode;

    @ApiModelProperty("不修改设备的通讯服务地址")
    private Boolean notEditDev;

    @ApiModelProperty("是否使用TCP")
    @JSONField(name = "isTcp")
    private Boolean useTcp;

    @ApiModelProperty("sdk模式下,服务器IP地址")
    private String sdkIp;

    @ApiModelProperty("sdk模式下,服务器端口")
    private Integer sdkPort;

    @ApiModelProperty("sdk模式下,服务器id")
    private Integer sdkId;

    @ApiModelProperty("http模式下,服务器URL")
    private String httpUrl;

    @ApiModelProperty("http模式下,自定义参数")
    private String httpSig;

    @ApiModelProperty("mqtt模式下,服务器IP或域名")
    private String mqttHost;

    @ApiModelProperty("mqtt模式下,服务器端口")
    private Integer mqttPort;

    @ApiModelProperty("mqtt模式下,心跳间隔")
    private Integer mqttHeart;

    @ApiModelProperty("mqtt模式下,用户名")
    private String mqttUser;

    @ApiModelProperty("mqtt模式下,密码")
    private String mqttPwd;

    @ApiModelProperty("mqtt模式下,Topic前缀")
    private String mqttTopic;

    @ApiModelProperty("mqtt模式下,TLS启用")
    private Boolean mqttTls;

    @ApiModelProperty("mqtt模式下,TLS使用私有CA")
    private Boolean mqttTlsCa;

    @ApiModelProperty("mqtt模式下,TLS使用私有证书")
    private Boolean mqttTlsCert;

    @ApiModelProperty("mqtt模式下,TLS使用强制验证")
    private Boolean mqttTlsAuth;

    @ApiModelProperty("mqtt模式下,TLS使用请求证书")
    private Boolean mqttTlsReqCert;

    @ApiModelProperty("websocket模式下,服务器地址")
    private String wsUrl;

    @ApiModelProperty("websocket模式下,origin参数")
    private Integer wsOrigin;

    @ApiModelProperty("使用http处理上传数据")
    private Boolean upToHttp;

}
