package com.weds.devmanages.entity;

import lombok.Data;

/**
 * 公共参数
 *
 * @author tjy
 **/
@Data
public class PublicParam {

    private Integer page;

    private Integer rows;

    private String sort;

    private String order;

    private Integer total;

}