package com.weds.devmanages.config.task;

import com.weds.devmanages.service.impl.base.DevBaseImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;


@Component
@Slf4j
public class DevTimedTask {

    @Autowired
    private DevBaseImpl devBase;

    @Scheduled(cron = "${weds.timed.task-state}")
    @Async("devTaskExecutor")
    public void taskState() {
        log.debug("=================== init dev state =================== ");
        // 根据定时任务同步设备状态（在线，离线）
        devBase.initDeviceState();
        devBase.tokenRegularInspection();
    }

    @Scheduled(cron = "${weds.timed.task-info}")
    @Async("devTaskExecutor")
    public void taskInfo() {
        log.debug("=================== init send dev =================== ");

        // 每10分钟同步设备信息信息
        devBase.initSendDevRequest();

        // 每10分钟同步设备应用信息
        devBase.initDeviceAppInfo();

        // 每10分钟同步设备运行信息
        devBase.initDeviceRunInfo();

        // 每10分钟同步设备磁盘信息
        devBase.initDeviceDiskInfo();

    }


}
