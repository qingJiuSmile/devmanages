package com.weds.devmanages.entity;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class RunInfoEntity {

    private Integer code;

    private String msg;

    private List<?> rows;

    private Integer total; //总数量

    private RunInfoData data;

    @Data
    public static class RunInfoData {

        private String devIp;

        private Integer startSec;

        private String devTime;

        private Long freeMem;

        private Long totalMem;

        @ApiModelProperty("剩余磁盘空间")
        private Long freeDisk;

        @ApiModelProperty("总磁盘空间")
        private Long totalDisk;

    }

}
