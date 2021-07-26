package com.weds.devmanages.entity.record;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;


/**
 * 人员档案信息
 *
 * @author tjy
 **/

@Data
public class DevArchives extends DevResultEntity {

    private List<ArchivesData> list;

    @Data
    public static class ArchivesData {
        /**
         * "auto_id":2,
         * "xh":"20000005",
         * "bh":"00046402342",
         * "xm":"张晓茜",
         * "kh":"20000005",
         * "zw":"0000000000",
         * "zp":0,
         * "mm":"123456",
         * "sr":"",
         * "gl":0,
         * "kssj":"2021-07-19 00:00:00",
         * "jssj":"2041-07-19 23:59:59",
         * "mj":"1",
         * "bm":"2018信息3班",
         * "rl":0,
         * "idcard":""
         */

        @ApiModelProperty("人员序号")
        @JSONField(name = "xh")
        private Long userSerial;

        @ApiModelProperty("工号")
        @JSONField(name = "bh")
        private String userNo;

        @ApiModelProperty("姓名")
        @JSONField(name = "xm")
        private String userName;

        @ApiModelProperty("卡号")
        @JSONField(name = "kh")
        private String cardNo;

        @ApiModelProperty("照片数量")
        @JSONField(name = "zp")
        private Integer photoNum;

        @ApiModelProperty("人脸数量")
        @JSONField(name = "rl")
        private Integer faceNum;

        @ApiModelProperty("有效开始时间")
        @JSONField(name = "kssj")
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
        private LocalDateTime beginTime;

        @ApiModelProperty("有效结束时间")
        @JSONField(name = "jssj")
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
        private LocalDateTime endTime;

        @ApiModelProperty("门禁规则")
        @JSONField(name = "mj")
        private Integer rule;

        @ApiModelProperty("部门名称")
        @JSONField(name = "bm")
        private String deptName;
    }

}