package org.ejatohvee.synthetichumancorestarter;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.ejatohvee.synthetichumancorestarter.aspect.WeylandWatchingYou;
import org.ejatohvee.synthetichumancorestarter.aspect.WeylandWatchingYouAspect;
import org.ejatohvee.synthetichumancorestarter.configs.KafkaAuditConfig;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.stereotype.Component;
import org.springframework.test.context.ActiveProfiles;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@EmbeddedKafka(partitions = 1, topics = {"synthetic-audit"})
@SpringBootTest
@ActiveProfiles("kafka-test")
public class KafkaAuditIntegrationTest {
    @Autowired
    private TestService testService;

    private static BlockingQueue<ConsumerRecord<String, String>> records;

    @BeforeAll
    static void setupQueue() {
        records = new LinkedBlockingQueue<>();
    }

    @KafkaListener(topics = "synthetic-audit", groupId = "test-group")
    public void listen(ConsumerRecord<String, String> record) {
        records.add(record);
    }

    @Test
    void whenAnnotatedMethodInvoked_auditGoesToKafka() throws Exception {
        testService.doWork(42);

        ConsumerRecord<String, String> rec = records.poll(5, java.util.concurrent.TimeUnit.SECONDS);
        assertThat(rec).isNotNull();
        String payload = rec.value();

        assertThat(payload).contains("\"method\":\"TestServiceImpl.doWork(..)\"");
        assertThat(payload).contains("\"arguments\":[42]");
        assertThat(payload).contains("\"timestampBefore\"");
        assertThat(payload).contains("\"timestampAfter\"");
    }

    @Configuration
    @ComponentScan(basePackageClasses = TestService.class)
    @Import({WeylandWatchingYouAspect.class, KafkaAuditConfig.class})
    static class TestConfig {}

    public interface TestService {
        int doWork(int x);
    }

    @Component
    public static class TestServiceImpl implements TestService {
        @WeylandWatchingYou
        @Override
        public int doWork(int x) {
            return x * 2;
        }
    }
}
