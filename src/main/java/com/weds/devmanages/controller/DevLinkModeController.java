package com.weds.devmanages.controller;

import com.weds.devmanages.base.BaseClass;
import com.weds.devmanages.config.log.JsonResult;
import com.weds.devmanages.entity.mode.DevNet;
import com.weds.devmanages.entity.mode.LinkMode;
import com.weds.devmanages.service.DevLinkMode;
import com.weds.devmanages.service.sign.Signature;
import com.weds.devmanages.util.IpConfig;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Api(tags = "设备联机设置", description = "设备联机设置")
@RequestMapping("/mode")
@RestController
@Signature
public class DevLinkModeController extends BaseClass {

    @Autowired
    private DevLinkMode devLinkMode;

    @ApiOperation("获取设备联机配置")
    @PostMapping("/getDevLinkMode/{ip}")
    public JsonResult<LinkMode> getDevLinkMode(@PathVariable String ip) {

        if (StringUtils.isBlank(ip)) {
            return failMsg("设备IP为空");
        }

        if (!IpConfig.ipCheck(ip)) {
            return failMsg("IP格式有误");
        }
        return succMsgData(devLinkMode.getLinkMode(ip));
    }

    @ApiOperation("设置设备联机配置")
    @PostMapping("/setUpDevLinkMode/{ip}")
    public JsonResult<Boolean> setUpDevLinkMode(@PathVariable String ip, @RequestBody LinkMode param) {

        if (param == null) {
            return failMsg("配置参数有误");
        }

        if (StringUtils.isBlank(ip)) {
            return failMsg("设备IP为空");
        }

        if (!IpConfig.ipCheck(ip)) {
            return failMsg("IP格式有误");
        }
        return succMsgData(devLinkMode.setUpLinkMode(ip, param));
    }

    @ApiOperation("获取设备网络配置")
    @PostMapping("/getDevNet/{ip}")
    public JsonResult<DevNet> getDevNet(@PathVariable String ip) {

        if (StringUtils.isBlank(ip)) {
            return failMsg("设备IP为空");
        }

        if (!IpConfig.ipCheck(ip)) {
            return failMsg("IP格式有误");
        }
        return succMsgData(devLinkMode.getDevNet(ip));
    }

    @ApiOperation("设置设备网络配置")
    @PostMapping("/setUpDevNet/{ip}")
    public JsonResult<Boolean> setUpDevNet(@PathVariable String ip, @RequestBody DevNet param) {

        if (param == null) {
            return failMsg("配置参数有误");
        }

        if (StringUtils.isBlank(ip)) {
            return failMsg("设备IP为空");
        }

        if (!IpConfig.ipCheck(ip)) {
            return failMsg("IP格式有误");
        }
        return succMsgData(devLinkMode.setUpDevNet(ip, param));
    }


}
