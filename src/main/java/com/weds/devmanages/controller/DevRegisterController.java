package com.weds.devmanages.controller;

import com.weds.devmanages.config.log.JsonResult;
import com.weds.devmanages.service.DevRegister;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Api(tags = "设备注册", description = "设备注册")
@RestController
@RequestMapping("/register")
public class DevRegisterController {

    @Autowired
    private DevRegister devRegister;

    @ApiOperation("设备注册,多个ip密码相同，使用','分隔")
    @PostMapping("/devRegister")
    public JsonResult<List<Map<String, Object>>> configureTheDevicePwd(@RequestParam(value = "pwd") String pwd,
                                                                       @RequestParam(value = "devIp") String devIp) {
        return devRegister.register(devIp, pwd);
    }
}
