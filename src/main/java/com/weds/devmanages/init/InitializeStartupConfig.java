package com.weds.devmanages.init;

import com.weds.devmanages.service.impl.N8Implement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

/**
 * 项目启动加载项
 **/

@Component
public class InitializeStartupConfig implements ApplicationRunner {

    @Autowired
    private N8Implement n8Implement;

    @Override
    public void run(ApplicationArguments args) throws Exception {

        n8Implement.initToken();
    }
}
