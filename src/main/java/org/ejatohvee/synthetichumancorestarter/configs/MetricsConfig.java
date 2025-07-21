package org.ejatohvee.synthetichumancorestarter.configs;

import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.actuate.autoconfigure.metrics.MeterRegistryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;

@Configuration
public class MetricsConfig {
    @Bean
    public Gauge queueSizeGauge(
            @Qualifier("commandExecutorService") ExecutorService executorService,
            MeterRegistry registry) {
        ThreadPoolExecutor threadPoolExecutor = (ThreadPoolExecutor) executorService;

        return Gauge
                .builder("android.queue.size", threadPoolExecutor, p -> p.getQueue().size())
                .description("Current number of commands waiting in the queue")
                .register(registry);
    }

    @Bean
    public MeterRegistryCustomizer<MeterRegistry> commonTagsCustomizer() {
        return registry -> registry.config()
                .commonTags("application", "synthetic-core-starter");
    }
}
