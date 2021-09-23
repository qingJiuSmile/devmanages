package com.weds.devmanages.controller;

import com.weds.devmanages.base.BaseClass;
import com.weds.devmanages.config.log.JsonResult;
import com.weds.devmanages.entity.record.*;
import com.weds.devmanages.service.DevRecord;
import com.weds.devmanages.service.sign.Signature;
import com.weds.devmanages.util.IpConfig;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Api(tags = "设备档案记录", description = "设备档案记录")
@RestController
@RequestMapping("/deviceRecord")
@Signature
public class DevRecordController extends BaseClass {

    @Autowired
    private DevRecord devRecord;

    @ApiOperation("获取门禁规则")
    @PostMapping("/getDevRule/{ip}")
    public JsonResult<DevRule> getDevRule(@RequestBody DevRecordParam param, @PathVariable String ip) {

        if (StringUtils.isBlank(ip)) {
            return failMsg("设备IP为空");
        }

        if (!IpConfig.ipCheck(ip)) {
            return failMsg("IP格式有误");
        }
        param.setDevIp(ip);
        return succMsgData(devRecord.getRule(param));
    }

    @ApiOperation("获取档案人员信息")
    @PostMapping("/getArchives/{ip}")
    public JsonResult<DevArchives> getArchives(@RequestBody DevRecordParam param, @PathVariable String ip) {

        if (StringUtils.isBlank(ip)) {
            return failMsg("设备IP为空");
        }

        if (!IpConfig.ipCheck(ip)) {
            return failMsg("IP格式有误");
        }
        param.setDevIp(ip);
        return succMsgData(devRecord.getArchives(param));
    }

    @ApiOperation("获取门禁时段")
    @PostMapping("/getTimeInterval/{ip}")
    public JsonResult<DevTimeInterval> getTimeInterval(@RequestBody DevRecordParam param, @PathVariable String ip) {

        if (StringUtils.isBlank(ip)) {
            return failMsg("设备IP为空");
        }

        if (!IpConfig.ipCheck(ip)) {
            return failMsg("IP格式有误");
        }
        param.setDevIp(ip);
        return succMsgData(devRecord.getTimeInterval(param));
    }

    @ApiOperation("获取人脸错误信息")
    @PostMapping("/getFaceErr/{ip}")
    public JsonResult<DevFaceErr> getFaceErr(@RequestBody DevRecordParam param, @PathVariable String ip) {

        if (StringUtils.isBlank(ip)) {
            return failMsg("设备IP为空");
        }

        if (!IpConfig.ipCheck(ip)) {
            return failMsg("IP格式有误");
        }
        param.setDevIp(ip);
        return succMsgData(devRecord.getFaceErr(param));
    }

    @ApiOperation("获取人脸查询记录信息")
    @PostMapping("/getFaceIdentify/{ip}")
    public JsonResult<DevIdentifyEntity> getFaceIdentify(@RequestBody DevRecordParam param, @PathVariable String ip) {

        if (StringUtils.isBlank(ip)) {
            return failMsg("设备IP为空");
        }

        if (!IpConfig.ipCheck(ip)) {
            return failMsg("IP格式有误");
        }
        param.setDevIp(ip);
        return succMsgData(devRecord.getFaceIdentify(param));
    }


    @ApiOperation("获取外来人脸查询记录信息")
    @PostMapping("/getOutsiders/{ip}")
    public JsonResult<DevOutsidersEntiy> getOutsiders(@RequestBody DevRecordParam param, @PathVariable String ip) {

        if (StringUtils.isBlank(ip)) {
            return failMsg("设备IP为空");
        }

        if (!IpConfig.ipCheck(ip)) {
            return failMsg("IP格式有误");
        }
        param.setDevIp(ip);
        return succMsgData(devRecord.getOutsiders(param));
    }

    @ApiOperation("清空剩余任务数")
    @GetMapping("/taskClearAll/{ip}")
    public JsonResult<Boolean> taskClearAll(@PathVariable String ip) {

        if (StringUtils.isBlank(ip)) {
            return failMsg("设备IP为空");
        }

        if (!IpConfig.ipCheck(ip)) {
            return failMsg("IP格式有误");
        }

        DevRecordParam param = new DevRecordParam();
        param.setDevIp(ip);
        return succMsgData(devRecord.taskClearAll(param));
    }

    @ApiOperation("清空应用信息（2.为上传记录 3.人脸 4.档案）")
    @GetMapping("/clearAll/{ip}")
    public JsonResult<Boolean> clearAll(@RequestParam("type") Integer type, @PathVariable String ip) {

        if (StringUtils.isBlank(ip)) {
            return failMsg("设备IP为空");
        }

        if (!IpConfig.ipCheck(ip)) {
            return failMsg("IP格式有误");
        }

        DevRecordParam param = new DevRecordParam();
        param.setDevIp(ip);
        param.setType(type);
        return succMsgData(devRecord.clearAll(param));
    }


}
