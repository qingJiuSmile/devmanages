package com.weds.devmanages.entity.record;

import com.alibaba.fastjson.annotation.JSONField;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;


/**
 * 设备时段规则实体
 *
 * @author tjy
 **/

@Data
public class DevRule extends DevResultEntity {

    private List<DataRule> list;

    @Data
    public static class DataRule {

        /**
         * {auto_id: 2, gz: "2", sd: "1002"}
         */

        @ApiModelProperty("规则号")
        @JSONField(name = "gz")
        private Integer ruleNo;

        @ApiModelProperty("时段号")
        @JSONField(name = "sd")
        private Integer dateTimeNo;
    }

}
