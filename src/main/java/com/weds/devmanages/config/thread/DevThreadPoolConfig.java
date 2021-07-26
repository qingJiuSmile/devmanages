package com.weds.devmanages.config.thread;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 线程池配置
 *
 * @author tjy
 **/
@Configuration
@ConfigurationProperties(prefix = "weds.thread")
@Data
public class DevThreadPoolConfig {

    private Integer corePoolSize = 10;

    private Integer maximumPoolSize = 10;

    @Bean("devTaskExecutor")
    public Executor taskExecutor() {

        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        // 核心线程数目
        executor.setCorePoolSize(corePoolSize);
        // 指定最大线程数
        executor.setMaxPoolSize(maximumPoolSize * Runtime.getRuntime().availableProcessors());
        // 队列中最大的数目
        executor.setQueueCapacity(maximumPoolSize * 2 * 10);
        // 线程空闲后的最大存活时间
        executor.setKeepAliveSeconds(60);
        // 线程名称前缀
        executor.setThreadNamePrefix("devExecutor-");
        // 设置线程池关闭的时候等待所有任务都完成后，再继续销毁其他的Bean，确保异步任务的销毁就会先于数据库连接池对象的销毁。
        executor.setWaitForTasksToCompleteOnShutdown(true);
        // 设置线程池中 任务的等待时间
        executor.setAwaitTerminationSeconds(60);
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.DiscardOldestPolicy());
        executor.initialize();
        return executor;
    }

}
