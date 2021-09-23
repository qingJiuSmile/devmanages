package com.weds.devmanages.init;

import com.weds.devmanages.service.impl.base.DevBaseImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * 项目启动加载项
 **/

@Component
public class InitializeStartupConfig implements ApplicationRunner {

    @Autowired
    private DevBaseImpl devBase;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        devBase.initToken();
        // 休眠5秒 给token获取时间
        TimeUnit.SECONDS.sleep(5);
        devBase.initSendDevRequest();
        devBase.initDeviceAppInfo();
        devBase.initDeviceRunInfo();
        devBase.initDeviceDiskInfo();
    }
}
