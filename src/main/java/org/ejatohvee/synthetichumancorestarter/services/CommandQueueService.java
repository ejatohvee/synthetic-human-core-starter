package org.ejatohvee.synthetichumancorestarter.services;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class CommandQueueService {
    private final ThreadPoolExecutor executor;
    private final MeterRegistry meterRegistry;

    public CommandQueueService(
            @Qualifier("commandExecutorService") ExecutorService executorService,
            MeterRegistry meterRegistry) {
        this.executor = (ThreadPoolExecutor) executorService;
        this.meterRegistry = meterRegistry;
    }

    public int getQueueSize() {
        return executor.getQueue().size();
    }

    public void markTaskCompleted(String commandAuthor) {
        Counter.builder("android.tasks.completed")
                .description("Number of completed tasks by author")
                .tag("author", commandAuthor)
                .register(meterRegistry)
                .increment();
    }
}
