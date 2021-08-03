package com.weds.devmanages.controller;

import cn.hutool.core.io.FileTypeUtil;
import com.weds.devmanages.base.BaseClass;
import com.weds.devmanages.config.log.JsonResult;
import com.weds.devmanages.entity.*;
import com.weds.devmanages.entity.record.RecordEntity;
import com.weds.devmanages.service.impl.base.DevBaseImpl;
import com.weds.devmanages.service.sign.Signature;
import com.weds.devmanages.util.Base64Utils;
import com.weds.devmanages.util.IpConfig;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import net.coobird.thumbnailator.Thumbnails;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.TimeUnit;


@Api(tags = "设备基础数据", description = "设备基础数据")
@RequestMapping("/base")
@RestController
public class DevBaseController extends BaseClass {

    @Autowired
    private DevBaseImpl n8Implement;

    private static final long MB = 1073741824L;

    @ApiOperation("获取所有设备硬件系统信息")
    @PostMapping("/getInfoAll")
    public JsonResult<SysInfoEntity> getInfoAll(@RequestBody PublicParam search) throws InterruptedException {
        n8Implement.asyncGetSysInfo();
        TimeUnit.MILLISECONDS.sleep(500);
        return succMsgData(n8Implement.getDevInfoMap(search));
    }

    @ApiOperation("获取某台设备硬件系统信息")
    @PostMapping("/getInfo/{ip}")
    @Signature
    public JsonResult<SysInfoEntity.SysData> getInfo(@PathVariable String ip) {

        if (StringUtils.isBlank(ip)) {
            return failMsg("设备IP为空");
        }

        if (!IpConfig.ipCheck(ip)) {
            return failMsg("IP格式有误");
        }
        return succMsgData(n8Implement.getSysInfo(ip));
    }

    @ApiOperation("获取所有设备应用信息")
    @PostMapping("/getAppInfoAll")
    public JsonResult<AppInfoEntity> getAppInfoAll(@RequestBody PublicParam search) throws InterruptedException {
        n8Implement.asyncGetAppInfo();
        TimeUnit.MILLISECONDS.sleep(500);
        return succMsgData(n8Implement.getDevAppInfoMap(search));
    }

    @ApiOperation("获取某台设备应用信息")
    @PostMapping("/getAppInfo/{ip}")
    @Signature
    public JsonResult<AppInfoEntity.AppInfoData> getAppInfo(@PathVariable String ip) {

        if (StringUtils.isBlank(ip)) {
            return failMsg("设备IP为空");
        }

        if (!IpConfig.ipCheck(ip)) {
            return failMsg("IP格式有误");
        }
        return succMsgData(n8Implement.getAppInfo(ip));
    }

    @ApiOperation("获取所有设备运行信息")
    @PostMapping("/getRunInfoAll")
    public JsonResult<RunInfoEntity> getRunInfoAll(@RequestBody PublicParam search) throws InterruptedException {
        n8Implement.asyncGetRunInfo();
        TimeUnit.MILLISECONDS.sleep(500);
        return succMsgData(n8Implement.getDevRunInfoMap(search));
    }

    @ApiOperation("获取某台设备运行信息")
    @PostMapping("/getRunInfo/{ip}")
    @Signature
    public JsonResult<RunInfoEntity.RunInfoData> getRunInfo(@PathVariable String ip) {

        if (StringUtils.isBlank(ip)) {
            return failMsg("设备IP为空");
        }

        if (!IpConfig.ipCheck(ip)) {
            return failMsg("IP格式有误");
        }
        return succMsgData(n8Implement.getRunInfo(ip));
    }

    @ApiOperation("获取所有设备磁盘信息")
    @PostMapping("/getDiskInfoAll")
    public JsonResult<DiskInfoEntity> getDiskInfoAll(@RequestBody PublicParam search) throws InterruptedException {
        n8Implement.asyncGetDiskInfo();
        TimeUnit.MILLISECONDS.sleep(500);
        return succMsgData(n8Implement.getDevDiskInfoMap(search));
    }

    @ApiOperation("获取某台设备磁盘信息")
    @PostMapping("/getDiskInfo/{ip}")
    public JsonResult<DiskInfoEntity.DiskInfoData> getDiskInfo(@PathVariable String ip) {

        if (StringUtils.isBlank(ip)) {
            return failMsg("设备IP为空");
        }

        if (!IpConfig.ipCheck(ip)) {
            return failMsg("IP格式有误");
        }
        return succMsgData(n8Implement.getDiskInfo(ip));
    }


    @ApiOperation("重启设备")
    @GetMapping("/restartTheDevice/{ip}")
    @Signature
    public JsonResult<Boolean> restartTheDevice(@PathVariable String ip) {
        if (StringUtils.isBlank(ip)) {
            return failMsg("设备ip为空");
        }

        if (!IpConfig.ipCheck(ip)) {
            return failMsg("IP格式有误");
        }
        return succMsgData(n8Implement.reStart(ip));
    }

    @ApiOperation("升级设备文件")
    @PostMapping("/deviceUpdate/{ip}")
    @Signature
    public JsonResult<Boolean> deviceUpdate(@PathVariable String ip, @RequestParam MultipartFile file) {
        if (StringUtils.isBlank(ip)) {
            return failMsg("设备ip为空");
        }
        if (!IpConfig.ipCheck(ip)) {
            return failMsg("IP格式有误");
        }
        return succMsgData(n8Implement.deviceUpdate(ip, file));
    }

    @ApiOperation(value = "获取设备信息", hidden = true)
    @PostMapping("/getDevRecord/{ip}")
    @Signature
    public JsonResult<RecordEntity.RecordData> getDevRecord(@PathVariable String ip, @RequestBody PublicParam search) {
        if (StringUtils.isBlank(ip)) {
            return failMsg("设备ip为空");
        }
        if (!IpConfig.ipCheck(ip)) {
            return failMsg("IP格式有误");
        }
        return succMsgData(n8Implement.searchRecord(ip, search));
    }


    @ApiOperation("获取设备开机照片")
    @PostMapping("/getStartUpImg/{ip}")
    @Signature
    public JsonResult<StartUpImg> getStartUpImg(@PathVariable String ip) {
        if (StringUtils.isBlank(ip)) {
            return failMsg("设备ip为空");
        }
        if (!IpConfig.ipCheck(ip)) {
            return failMsg("IP格式有误");
        }
        return succMsgData(n8Implement.getStartUpImg(ip));
    }

    @ApiOperation("设置设备开机照片")
    @PostMapping("/importStartUpImg/{ip}")
    @Signature
    public JsonResult<Boolean> importStartUpImg(@PathVariable String ip, MultipartFile file) {

        // TODO 限制同一时间只能有一人进行图片设置
        if (StringUtils.isBlank(ip)) {
            return failMsg("设备ip为空");
        }
        if (!IpConfig.ipCheck(ip)) {
            return failMsg("IP格式有误");
        }

        String base64Img;
        if (file == null) {
            return failMsg("文件为空");
        } else {
            File targetFile = null;
            try {
                String type = FileTypeUtil.getType(file.getInputStream());
                if (!"jpg".equals(type) && !"png".equals(type) && !"jpeg".equals(type)) {
                    return failMsg("请输入正确的图片格式");
                }

                String originalFilename = file.getOriginalFilename();
                if (StringUtils.isBlank(originalFilename)) {
                    return failMsg("文件有误");
                }

                // 查询当前设备的屏幕分辨率
                SysInfoEntity.SysData sysInfo = n8Implement.getSysInfo(ip);

                if (sysInfo == null || sysInfo.getScrHeight() == null || sysInfo.getScrWidth() == null) {
                    return failMsg("设置照片失败");
                }

                // 生成处理的临时文件； 将图片存入本地
                String newImgPath = System.getProperty("user.dir") +
                        File.separator;
                String imgPathRandom = ip + "-" + UUID.randomUUID() + ".png";
                newImgPath = newImgPath + imgPathRandom;

                targetFile = new File(System.getProperty("user.dir"), imgPathRandom);
                file.transferTo(targetFile);

                Thumbnails.of(newImgPath)
                        .size(sysInfo.getScrWidth(), sysInfo.getScrHeight())
                        //.keepAspectRatio(false)
                        .toFile(newImgPath);

                // 将图片生成base64
                base64Img = Base64Utils.ImageToBase64ByLocal(newImgPath);

            } catch (IOException e) {
                return failMsg(500, e.getMessage(), null);
            } finally {
                if (targetFile != null) {
                    // 使用完临时图片后删除
                    targetFile.delete();
                }
            }
        }
        return succMsgData(n8Implement.setUpStartUpImg(ip, base64Img));
    }

    @ApiOperation("删除设备开机照片")
    @PostMapping("/deleteStartUpImg/{ip}")
    @Signature
    public JsonResult<Boolean> deleteStartUpImg(@PathVariable String ip) {

        if (StringUtils.isBlank(ip)) {
            return failMsg("设备ip为空");
        }
        if (!IpConfig.ipCheck(ip)) {
            return failMsg("IP格式有误");
        }
        return succMsgData(n8Implement.setUpStartUpImg(ip, ""));
    }

    @ApiOperation("设备时间校准 (设备自带校时，此功能无效)")
    @PostMapping("/setTime/{ip}")
    @Signature
    public JsonResult<Boolean> setTime(@PathVariable String ip) {

        if (StringUtils.isBlank(ip)) {
            return failMsg("设备ip为空");
        }
        if (!IpConfig.ipCheck(ip)) {
            return failMsg("IP格式有误");
        }
        return succMsgData(n8Implement.timeCalibration(ip));
    }


}
