package com.weds.devmanages.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class N8LoginEntity {

    private Integer code;

    private String msg;

    private DataBean data;

    @Data
    public static class DataBean {

        @JsonProperty(value = "jtoken")
        private String token;

        private Integer loginLevel;

        private Integer expiresIdle;

        @JsonProperty(value = "jacl")
        private List<String> userLs;

    }
}
