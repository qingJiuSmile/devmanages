package com.weds.devmanages.entity;

import lombok.Data;


@Data
public class SignatureEntity {

    private String appId;

    private String appSecret;

    private String timestamp;
}
