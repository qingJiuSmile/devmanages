package com.weds.devmanages.controller;

import com.weds.devmanages.base.BaseClass;
import com.weds.devmanages.config.log.JsonResult;
import com.weds.devmanages.entity.config.DevFaceConfig;
import com.weds.devmanages.entity.config.DevPwdConfig;
import com.weds.devmanages.entity.config.DevStdConfig;
import com.weds.devmanages.entity.config.DevViewConfig;
import com.weds.devmanages.service.DevConfiguration;
import com.weds.devmanages.service.sign.Signature;
import com.weds.devmanages.util.IpConfig;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Api(tags = "设备配置", description = "设备配置")
@RequestMapping("/config")
@RestController
@Signature
public class DevConfigurationController extends BaseClass {

    @Autowired
    private DevConfiguration devConfiguration;


    @ApiOperation("获取设备标准配置信息")
    @PostMapping("/getDevStdConfig/{ip}")
    public JsonResult<DevStdConfig> getDevStdConfig(@PathVariable String ip) {

        if (StringUtils.isBlank(ip)) {
            return failMsg("设备IP为空");
        }

        if (!IpConfig.ipCheck(ip)) {
            return failMsg("IP格式有误");
        }
        return succMsgData(devConfiguration.getDevStdConfig(ip));
    }


    @ApiOperation("设置设备标准配置")
    @PostMapping("/setUpDevConfig/{ip}")
    public JsonResult<Boolean> configureTheDevice(@PathVariable String ip, @RequestBody DevStdConfig param) {

        if (param == null) {
            return failMsg("配置参数有误");
        }

        if (StringUtils.isBlank(ip)) {
            return failMsg("设备IP为空");
        }

        if (!IpConfig.ipCheck(ip)) {
            return failMsg("IP格式有误");
        }
        return succMsgData(devConfiguration.configureTheDevice(ip, param));
    }


    @ApiOperation("获取设备人脸配置信息")
    @PostMapping("/getDevFaceConfig/{ip}")
    public JsonResult<DevFaceConfig> getDevFaceConfig(@PathVariable String ip) {

        if (StringUtils.isBlank(ip)) {
            return failMsg("设备IP为空");
        }

        if (!IpConfig.ipCheck(ip)) {
            return failMsg("IP格式有误");
        }
        return succMsgData(devConfiguration.getDevFaceConfig(ip));
    }


    @ApiOperation("设置设备人脸配置")
    @PostMapping("/setUpDevFaceConfig/{ip}")
    public JsonResult<Boolean> configureTheDeviceFace(@PathVariable String ip, @RequestBody DevFaceConfig param) {

        if (param == null) {
            return failMsg("配置参数有误");
        }

        if (StringUtils.isBlank(ip)) {
            return failMsg("设备IP为空");
        }

        if (!IpConfig.ipCheck(ip)) {
            return failMsg("IP格式有误");
        }
        return succMsgData(devConfiguration.configureDeviceFace(ip, param));
    }

    @ApiOperation("获取设备界面配置信息")
    @PostMapping("/getDevViewConfig/{ip}")
    public JsonResult<DevViewConfig> getDevViewConfig(@PathVariable String ip) {

        if (StringUtils.isBlank(ip)) {
            return failMsg("设备IP为空");
        }

        if (!IpConfig.ipCheck(ip)) {
            return failMsg("IP格式有误");
        }
        return succMsgData(devConfiguration.getDevViewConfig(ip));
    }


    @ApiOperation("设置设备界面配置")
    @PostMapping("/setUpDevViewConfig/{ip}")
    public JsonResult<Boolean> configureTheDeviceView(@PathVariable String ip, @RequestBody DevViewConfig param) {

        if (param == null) {
            return failMsg("配置参数有误");
        }

        if (StringUtils.isBlank(ip)) {
            return failMsg("设备IP为空");
        }

        if (!IpConfig.ipCheck(ip)) {
            return failMsg("IP格式有误");
        }
        return succMsgData(devConfiguration.configureDeviceView(ip, param));
    }

    @ApiOperation("获取设备门禁密码配置信息")
    @PostMapping("/getDevPwdConfig/{ip}")
    public JsonResult<DevPwdConfig> getDevPwdConfig(@PathVariable String ip) {

        if (StringUtils.isBlank(ip)) {
            return failMsg("设备IP为空");
        }

        if (!IpConfig.ipCheck(ip)) {
            return failMsg("IP格式有误");
        }
        return succMsgData(devConfiguration.getDevPwdConfig(ip));
    }


    @ApiOperation("设置设备门禁密码配置(只能是数字)")
    @PostMapping("/setUpDevPwdConfig/{ip}")
    public JsonResult<Boolean> configureTheDevicePwd(@PathVariable String ip, @RequestBody DevPwdConfig param) {

        if (param == null) {
            return failMsg("配置参数有误");
        }

        if (StringUtils.isBlank(ip)) {
            return failMsg("设备IP为空");
        }

        if (!IpConfig.ipCheck(ip)) {
            return failMsg("IP格式有误");
        }
        return succMsgData(devConfiguration.configureDevicePwd(ip, param));
    }
}
