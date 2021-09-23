package com.weds.devmanages.entity.record;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;


/**
 * 识别记录实体
 *
 * @author tjy
 **/

@Data
public class DevIdentifyEntity extends DevResultEntity {


    @JsonProperty("rows")
    private List<DataFaceIdent> list;

    private Long total;

    @Data
    public static class DataFaceIdent {
        /**
         * auto_id : 1
         * iden_time : 2021-09-09 09:11:07
         * user_id : 20003172
         * user_name : 田佳宇
         * user_bh : 10840
         * user_dep : 2018级专业一班
         * sn_num : 1
         * card : C14BE654
         * user_type : 0
         * att_dir : 2
         * auth_type : l
         * door_open : 0
         * photo_name : jk786671
         * ex_param :
         * face_rect : X201Y194W256H295
         * body_temp :
         * body_state :
         * tel :
         * gps :
         * ex_info :
         * serial :
         * face_score : 82.23
         * mj : 1
         * online_ret :
         */

        @JSONField(name = "auto_id")
        private Integer autoId;

        @JSONField(name = "iden_time")
        private String idenTime;

        @JSONField(name = "user_id")
        private String userId;

        @JSONField(name = "userName")
        private String userName;

        @JSONField(name = "user_bh")
        private String userBh;

        @JSONField(name = "user_dep")
        private String userDep;

        @JSONField(name = "sn_num")
        private Integer snNum;

        private String card;

        @JSONField(name = "user_type")
        private String userType;

        @JSONField(name = "att_dir")
        @ApiModelProperty("进出门方向0:自动,1:进门,2:出门")
        private Integer attDir;

        @ApiModelProperty("进出门方向")
        private String attDirStr;

        @JSONField(name = "auth_type")
        private String authType;

        @JSONField(name = "door_open")
        @ApiModelProperty("0:未开门, 1: 已开门")
        private Integer doorOpen;

        @ApiModelProperty("门状态")
        private String doorOpenStr;

        @JSONField(name = "photo_name")
        private String photoName;

        @JSONField(name = "ex_param")
        private String exParam;

        @JSONField(name = "face_rect")
        private String faceRect;

        @JSONField(name = "body_temp")
        private String bodyTemp;

        @JSONField(name = "body_state")
        private String bodyState;

        private String tel;

        private String gps;

        @JSONField(name = "ex_info")
        private String exInfo;

        private String serial;

        @JSONField(name = "face_score")
        private String faceScore;

        private String mj;

        @JSONField(name = "online_ret")
        private String onlineRet;
    }


}
