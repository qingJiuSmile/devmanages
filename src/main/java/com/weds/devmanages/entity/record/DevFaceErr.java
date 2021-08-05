package com.weds.devmanages.entity.record;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 人脸错误信息
 *
 * @author tjy
 **/
@Data
public class DevFaceErr extends DevResultEntity {

    @JsonProperty("rows")
    private List<DataFaceErr> list;

    private Long total;

    @Data
    public static class DataFaceErr {

        /**
         * "auto_id":1,
         * "xh":"40102100",
         * "jerr":-4002,
         * "jtime":"2021-07-19 10:07:56"
         * <p>
         * -userSerial ： String（人员序号）
         * -errCode :  String（错误值）
         * -recTime : LocalDateTime（记录时间）
         */

        @ApiModelProperty("人员序号")
        @JSONField(name = "xh")
        private String userSerial;

        @ApiModelProperty("错误值")
        @JSONField(name = "jerr")
        private String errCode;

        @ApiModelProperty("记录时间")
        @JSONField(name = "jtime")
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
        private LocalDateTime recTime;

    }

}
