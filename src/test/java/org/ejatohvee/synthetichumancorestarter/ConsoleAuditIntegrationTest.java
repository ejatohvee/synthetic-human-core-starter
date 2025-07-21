package org.ejatohvee.synthetichumancorestarter;

import org.ejatohvee.synthetichumancorestarter.aspect.WeylandWatchingYou;
import org.ejatohvee.synthetichumancorestarter.aspect.WeylandWatchingYouAspect;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.stereotype.Component;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith({SpringExtension.class, OutputCaptureExtension.class})
@SpringBootTest
@ActiveProfiles("console-test")
public class ConsoleAuditIntegrationTest {

    @Autowired
    private TestService testService;

    @Test
    void whenAnnotatedMethodInvoked_auditGoesToLog(CapturedOutput output) {
        testService.doSomething("hello");
        String logs = output.getOut();
        assertTrue(logs.matches("(?s).*\"method\"\\s*:\\s*\".*doSomething.*\".*"));
        assertTrue(logs.contains("\"arguments\":[\"hello\"]"));
        assertTrue(logs.contains("\"timestampBefore\""));
    }

    @Configuration
    @ComponentScan(basePackageClasses = TestService.class)
    @Import(WeylandWatchingYouAspect.class)
    static class TestConfig {}

    public interface TestService {
        void doSomething(String arg);
    }

    @Component
    public static class TestServiceImpl implements TestService {
        @WeylandWatchingYou
        @Override
        public void doSomething(String arg) {
        }
    }
}