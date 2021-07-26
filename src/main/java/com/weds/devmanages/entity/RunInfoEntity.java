package com.weds.devmanages.entity;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class RunInfoEntity {

    private List<?> rows;

    private Integer total;

    @Data
    public static class RunInfoData {

        private String devIp;

        @ApiModelProperty("开始秒")
        private Integer startSec;

        @ApiModelProperty("开机时间")
        private String devTime;

        @ApiModelProperty("剩余内存")
        private Long freeMem;

        @ApiModelProperty("总内存")
        private Long totalMem;

        @ApiModelProperty("剩余磁盘空间")
        private Long freeDisk;

        @ApiModelProperty("总磁盘空间")
        private Long totalDisk;

    }

}
