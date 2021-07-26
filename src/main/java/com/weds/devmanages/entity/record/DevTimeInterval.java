package com.weds.devmanages.entity.record;

import com.alibaba.fastjson.annotation.JSONField;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

/**
 * 门禁时段
 *
 * @author tjy
 **/
@Data
public class DevTimeInterval extends DevResultEntity {


    private List<DataTimeInterval> list;

    @Data
    public static class DataTimeInterval {

        /**
         * "auto_id":1,
         * "sd":"1001",
         * "dateStart":"2021-01-01",
         * "dateEnd":"2039-12-31",
         * "monthStart":1,
         * "monthEnd":12,
         * "dayStart":1,
         * "dayEnd":31,
         * "weekStart":1,
         * "weekEnd":7,
         * "timeStart":"00:00:00",
         * "timeEnd":"23:59:59",
         * "openUsers":1
         */

        @ApiModelProperty("时段号")
        @JSONField(name = "sd")
        private Integer dateTimeNo;

        @ApiModelProperty("有效期开始 yyyy-MM-dd")
        @JSONField(name = "dateStart")
        private LocalDate beginDate;

        @ApiModelProperty("有效期结束 yyyy-MM-dd")
        @JSONField(name = "dateEnd")
        private LocalDate endDate;

        @ApiModelProperty("月开始")
        @JSONField(name = "monthStart")
        private Integer beginMonth;

        @ApiModelProperty("月结束")
        @JSONField(name = "monthEnd")
        private Integer endMonth;

        @ApiModelProperty("日开始")
        @JSONField(name = "dayStart")
        private Integer beginDay;

        @ApiModelProperty("日结束")
        @JSONField(name = "dayEnd")
        private Integer endDay;

        @ApiModelProperty("星期开始")
        @JSONField(name = "weekStart")
        private Integer beginWeek;

        @ApiModelProperty("星期结束")
        @JSONField(name = "weekEnd")
        private Integer endWeek;

        @ApiModelProperty("开始时间HH:mm:ss")
        @JSONField(name = "timeStart")
        private LocalTime beginTime;

        @ApiModelProperty("结束时间HH:mm:ss")
        @JSONField(name = "timeEnd")
        private LocalTime endTime;
    }
}
