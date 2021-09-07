package com.weds.devmanages.entity.signature;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 签名配置参数
 *
 * @author tjy
 **/

@Data
@Component
@ConfigurationProperties("weds.sign")
public class SignatureParamEntity {

    private Boolean check;

    private String[] pathPatterns;
}
