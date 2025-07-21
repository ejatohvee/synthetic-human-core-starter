package org.ejatohvee.synthetichumancorestarter.configs;

import org.ejatohvee.synthetichumancorestarter.exceptions.QueueFullException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.*;

@Configuration
public class ThreadPoolConfig {

    @Value("${synthetic.core.thread-pool.size}")
    int corePoolSize;

    @Value("${synthetic.core.thread-pool.maxsize}")
    int maximumPoolSize;

    @Value("${synthetic.core.queue.capacity}")
    private int queueCapacity;

    @Bean
    public ExecutorService commandExecutorService() {
        RejectedExecutionHandler handler = (r, executor) -> {
            throw new QueueFullException(
                    String.format("Command queue is full (capacity=%d)", queueCapacity)
            );
        };

        ThreadPoolExecutor executor = new ThreadPoolExecutor(
                corePoolSize,
                maximumPoolSize,
                0L,
                TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<>(queueCapacity),
                handler);

        return executor;
    }
}
