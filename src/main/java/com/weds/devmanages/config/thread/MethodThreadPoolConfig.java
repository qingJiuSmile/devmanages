package com.weds.devmanages.config.thread;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 普通方法线程池管理
 *
 * @author tjy
 * @date 2021/9/11
 **/

@Configuration
@Slf4j
public class MethodThreadPoolConfig {

    private Integer corePoolSize = 10;

    private Integer maximumPoolSize = 10;

    @Bean("methodThreadPoll")
    public ThreadPoolExecutor instance() {
        return new ThreadPoolExecutor(
                corePoolSize,
                maximumPoolSize * Runtime.getRuntime().availableProcessors(),
                60L,
                TimeUnit.SECONDS,
                new SynchronousQueue<>(),
                new ThreadFactoryBuilder().setNameFormat("method-pool-%d").setDaemon(true).build(),
                new ThreadPoolExecutor.DiscardOldestPolicy()
        );
    }

}
