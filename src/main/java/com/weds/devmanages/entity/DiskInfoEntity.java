package com.weds.devmanages.entity;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * 设备磁盘空间实体
 *
 * @author tjy
 **/

@Data
public class DiskInfoEntity {


    private List<?> rows;

    private Integer total; //总数量

    @Data
    public static class DiskInfoData {

        private String devIp;

        @ApiModelProperty("剩余磁盘空间")
        private Long freeDisk;

        @ApiModelProperty("总磁盘空间")
        private Long totalDisk;
    }
}
