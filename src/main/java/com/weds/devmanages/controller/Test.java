package com.weds.devmanages.controller;

import com.weds.devmanages.base.BaseClass;
import com.weds.devmanages.config.log.JsonResult;
import com.weds.devmanages.entity.*;
import com.weds.devmanages.service.impl.N8Implement;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.util.concurrent.TimeUnit;

@Api(tags = "测试", description = "测试")
@RequestMapping("/test")
@RestController
public class Test extends BaseClass {


    @Autowired
    private N8Implement n8Implement;

    @ApiOperation("测试")
    @PostMapping("/admin")
    public void test() {

    }

    @ApiOperation("获取硬件系统信息")
    @PostMapping("/getInfo")
    public JsonResult<SysInfoEntity> getInfo(@RequestBody PublicParam search) throws InterruptedException {
        n8Implement.asyncGetSysInfo();
        TimeUnit.MILLISECONDS.sleep(500);
        return succMsgData(n8Implement.getDevInfoMap(search));
    }

    @ApiOperation("获取应用信息")
    @PostMapping("/getAppInfo")
    public JsonResult<AppInfoEntity> getAppInfo(@RequestBody PublicParam search) throws InterruptedException {
        n8Implement.asyncGetAppInfo();
        TimeUnit.MILLISECONDS.sleep(500);
        return succMsgData(n8Implement.getDevAppInfoMap(search));
    }

    @ApiOperation("获取运行信息")
    @PostMapping("/getRunInfo")
    public JsonResult<RunInfoEntity> getRunInfo(@RequestBody PublicParam search) throws InterruptedException {
        n8Implement.asyncGetRunInfo();
        TimeUnit.MILLISECONDS.sleep(500);
        return succMsgData(n8Implement.getDevRunInfoMap(search));
    }

    @ApiOperation("获取磁盘信息")
    @PostMapping("/getDiskInfo")
    public JsonResult<DiskInfoEntity> getDiskInfo(@RequestBody PublicParam search) throws InterruptedException {
        n8Implement.asyncGetDiskInfo();
        TimeUnit.MILLISECONDS.sleep(500);
        return succMsgData(n8Implement.getDevDiskInfoMap(search));
    }

    @ApiOperation("重启设备")
    @GetMapping("/restartTheDevice/{ip}")
    public JsonResult<Boolean> restartTheDevice(@PathVariable String ip) {
        if (StringUtils.isBlank(ip)) {
            return failMsg("设备ip为空");
        }
        return succMsgData(n8Implement.reStart(ip));
    }

    @ApiOperation("更新设备文件")
    @PostMapping("/deviceUpdate/{ip}")
    public JsonResult<Boolean> deviceUpdate(@PathVariable String ip, String password, MultipartFile file) {
        System.out.print("进入程序 ======================================================");
        if (StringUtils.isBlank(ip)) {
            return failMsg("设备ip为空");
        }
        return succMsgData(n8Implement.deviceUpdate(ip,password,file));
    }

}
