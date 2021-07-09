package com.weds.devmanages.util;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;


/**
 * 延迟阻塞队列
 *
 * @author tjy
 **/
@Slf4j
@Component
public class TaskQueueDaemonThread {

    private static final AtomicLong ATOMIC = new AtomicLong(0);


   /* private volatile static TaskQueueDaemonThread taskQueueDaemonThread;

    private TaskQueueDaemonThread() {
    }

    *//**
     * 单例
     *//*
    public static TaskQueueDaemonThread getInstance() {
            if (taskQueueDaemonThread == null) {
                synchronized (TaskQueueDaemonThread.class) {
                    if (taskQueueDaemonThread == null) {
                        taskQueueDaemonThread = new TaskQueueDaemonThread();
                    }
                }
            }
            return taskQueueDaemonThread;
        }*/

    /**
     * 创建一个自适应服务器配置的线程池
     */
    private ThreadPoolExecutor eventQueueService = new ThreadPoolExecutor(
            20,
            20 * Runtime.getRuntime().availableProcessors(),
            60L,
            TimeUnit.SECONDS,
            // 这里可以根据实际业务设定队列数量，默认大小是Integer.MAX_VALUE
            new LinkedBlockingDeque<>(),
            new ThreadFactoryBuilder().setNameFormat("delayed-pool-%d").setDaemon(true).build(),
            new ThreadPoolExecutor.DiscardOldestPolicy()
    );


    /**
     * 创建一个最初为空的新 DelayQueue
     */
    private DelayQueue<Task<Runnable>> t = new DelayQueue<>();


    /**
     * 初始化守护线程
     */
     @PostConstruct
    public void init() {
        // 初始化时，创建一个监听任务线程，避免主线程阻塞
        Thread daemonThread = new Thread(() -> execute());
        daemonThread.setDaemon(true);
        daemonThread.setName("delayed-pool-main");
        daemonThread.start();
    }

    /**
     * 初始化轮询执行任务
     */
    private void execute() {
        while (true) {
            try {
                // 加入执行间隔，每隔1s轮询一次
               // TimeUnit.SECONDS.sleep(1);

                // 从延迟队列中取值,如果没有对象过期则队列一直等待，
                Task<Runnable> t1 = t.take();

                // 修改问题的状态
                Runnable task = t1.getTask();
                if (task == null) {
                    continue;
                }

                eventQueueService.execute(task);
                log.info("result ==> [{}]", Thread.currentThread().getName());

            } catch (Exception e) {
                log.error(e.getMessage(), e);
                break;
            }
        }
    }


    /**
     * 添加任务，
     * time 延迟时间
     * task 任务
     * 用户为问题设置延迟时间
     */
    public void put(long time, Runnable task) {
        //转换成ns
        long nanoTime = TimeUnit.NANOSECONDS.convert(time, TimeUnit.MILLISECONDS);
        //创建一个任务
        Task<Runnable> k = new Task<>(nanoTime, task);
        //将任务放在延迟的队列中
        t.put(k);
    }

    /**
     * 结束任务，清空队列
     *
     * @param task
     */
    public boolean endTask(Task<Runnable> task) {
        return t.remove(task);
    }


   public class Task<T extends Runnable> implements Delayed {
        /**
         * 到期时间
         */
        private final long time;

        /**
         * 问题对象
         */
        private final T task;

        private final long n;

        public Task(long timeout, T t) {
            this.time = System.nanoTime() + timeout;
            this.task = t;
            this.n = ATOMIC.getAndIncrement();
        }

        /**
         * 返回与此对象相关的剩余延迟时间，以给定的时间单位表示
         */
        @Override
        public long getDelay(TimeUnit unit) {
            return unit.convert(this.time - System.nanoTime(), TimeUnit.NANOSECONDS);
        }

        @Override
        public int compareTo(Delayed other) {
            // compare zero ONLY if same object
            if (other == this) {
                return 0;
            }
            if (other instanceof Task) {
                Task x = (Task) other;
                long diff = time - x.time;
                if (diff < 0) {
                    return -1;
                } else if (diff > 0) {
                    return 1;
                } else if (n < x.n) {
                    return -1;
                } else {
                    return 1;
                }
            }
            long d = (getDelay(TimeUnit.NANOSECONDS) - other.getDelay(TimeUnit.NANOSECONDS));
            return (d == 0) ? 0 : ((d < 0) ? -1 : 1);
        }

        public T getTask() {
            return this.task;
        }

        @Override
        public int hashCode() {
            return task.hashCode();
        }

        @Override
        public boolean equals(Object object) {
            if (object instanceof Task) {
                return object.hashCode() == hashCode();
            }
            return false;
        }

    }
}
