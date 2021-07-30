package com.weds.devmanages.entity.config;

import com.alibaba.fastjson.annotation.JSONField;
import com.weds.devmanages.entity.SignatureEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 设备界面配置实体
 *
 * @author tjy
 **/
@Data
public class DevViewConfig extends SignatureEntity {

    /**
     * -enGoodVoice：Integer（0-禁用，1-识别成功后，播放上午好等问候语）
     * -showPhotoType : Integer（0-拍照照片，1-档案照片）
     * -showOkDelay：Integer（ 成功提示框显示时长, 单位：秒）
     * -pictureQuality : Integer（照片质量 20~95）
     */

    @ApiModelProperty("0-禁用，1-识别成功后，播放上午好等问候语")
    @JSONField(name = "en_good_voice")
    private Integer enGoodVoice;

    @ApiModelProperty("播放上午好等问候语默认值")
    @JSONField(name = "en_good_voiceDef")
    private Integer enGoodVoiceDef;

    @ApiModelProperty("0-拍照照片，1-档案照片")
    @JSONField(name = "show_photo_type")
    private Integer showPhotoType;

    @ApiModelProperty("成功提示框显示时长, 单位：秒")
    @JSONField(name = "show_ok_delay")
    private Integer showOkDelay;

    @ApiModelProperty("照片质量 20~95")
    @JSONField(name = "picture_quality")
    private Integer pictureQuality;
}
