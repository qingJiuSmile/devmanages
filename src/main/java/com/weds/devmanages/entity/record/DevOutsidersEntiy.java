package com.weds.devmanages.entity.record;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.weds.devmanages.entity.record.DevResultEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;


/**
 * 外来人员识别记录实体
 *
 * @author tjy
 **/

@Data
public class DevOutsidersEntiy extends DevResultEntity {


    @JsonProperty("rows")
    private List<OutDataFaceIdent> list;

    private Long total;

    @Data
    public static class OutDataFaceIdent {
        /**
         * auto_id : 1
         * iden_time : 2021-09-10 10:21:27
         * rec_state : 0
         * iden_card : 37061319000728683X
         * att_dir : 2
         * user_name : 单博梁
         * user_sex : 男
         * valid_date : 20170323-20370323
         * nation : 汉
         * address : 山东省烟台市莱山区某地
         * photo_name : jk692875
         * iden_photo : 37061319000728683X
         * body_temp :
         * body_state :
         * tel :
         * face_rect : X113Y400W239H176
         * hCode_area :
         * hCode_val :
         * online_ret :
         */

        @JSONField(name = "auto_id")
        private Integer autoId;

        @JSONField(name = "iden_time")
        private String idenTime;

        @JSONField(name = "rec_state")
        private String recState;

        @JSONField(name = "iden_card")
        private String idenCard;

        @JSONField(name = "att_dir")
        private Integer attDir;

        private String attDirStr;

        @JSONField(name = "user_name")
        private String userName;

        @JSONField(name = "user_sex")
        private String userSex;

        @JSONField(name = "valid_date")
        private String validDate;

        private String nation;

        private String address;

        @JSONField(name = "photo_name")
        private String photoName;

        @JSONField(name = "iden_photo")
        private String idenPhoto;

        @JSONField(name = "body_temp")
        private String bodyTemp;

        @JSONField(name = "body_state")
        private String bodyState;

        private String tel;

        @JSONField(name = "face_rect")
        private String faceRect;

        @JSONField(name = "hCode_area")
        private String hCodeArea;

        @JSONField(name = "hCode_val")
        private String hCodeVal;

        @JSONField(name = "online_ret")
        private String online_ret;


    }


}
