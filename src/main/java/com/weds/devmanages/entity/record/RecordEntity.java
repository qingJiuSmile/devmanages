package com.weds.devmanages.entity.record;

import com.alibaba.fastjson.annotation.JSONField;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * 设备记录实体
 *
 * @author tjy
 **/
@Data
public class RecordEntity {

    private Integer code;

    private String msg;

    private RecordData data;

    private List<?> rows;

    private Integer total; //总数量


    @Data
    public static class RecordData {

        private List<RecordList> list;

        private RecordPagination pagination;

        @Data
        public static class RecordList {

            @ApiModelProperty("人员序号")
            private String pid;

            @JSONField(name = "rec_time")
            @ApiModelProperty("记录时间")
            private String recTime;

            @JSONField(name = "iden_direction")
            @ApiModelProperty("识别方向")
            private String idenDirection;

            @ApiModelProperty("拍照名称")
            private String photo;

            @JSONField(name = "dev_id")
            @ApiModelProperty("设备id")
            private String devId;

            @JSONField(name = "card_no")
            @ApiModelProperty("卡号")
            private String cardNo;
        }

        @Data
        public static class RecordPagination {
            private Long current;

            private Integer pageSize;

            private Long total;
        }

    }


}
