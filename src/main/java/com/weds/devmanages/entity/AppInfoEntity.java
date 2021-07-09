package com.weds.devmanages.entity;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * 应用信息实体
 *
 * @author tjy
 **/

@Data
public class AppInfoEntity {

    private String msg;

    private Integer code;

    private AppInfoData data;

    private List<?> rows;

    private Integer total; //总数量

    @Data
    public static class AppInfoData {

        private String devIp;

        @ApiModelProperty("档案数量")
        private Integer docCount;

        @ApiModelProperty("人脸数量")
        private Integer faceCount;

        @ApiModelProperty("未上传记录数量")
        private Integer remainRecord;

        @ApiModelProperty("未上传文件数量")
        private Integer remainUpFile;

        @ApiModelProperty("未上传操作日志数量")
        private Integer remainOplog;

    }
}
