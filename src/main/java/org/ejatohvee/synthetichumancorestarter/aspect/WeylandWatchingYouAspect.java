package org.ejatohvee.synthetichumancorestarter.aspect;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;


import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import static org.apache.kafka.common.requests.FetchMetadata.log;

@Aspect
@Component
@ConditionalOnProperty(prefix = "synthetic.audit", name = "enabled", havingValue = "true", matchIfMissing = true)
public class WeylandWatchingYouAspect {
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final String kafkaTopic;
    private final AuditMode auditMode;
    private final ObjectMapper objectMapper;

    public enum AuditMode {KAFKA, CONSOLE}

    public WeylandWatchingYouAspect(
            @Value("${synthetic.audit.mode:kafka}") String mode,
            @Value("${synthetic.audit.kafka-topic}") String kafkaTopic,
            ObjectMapper objectMapper,
            @Autowired(required = false) KafkaTemplate<String, String> kafkaTemplate
    ) {
        this.kafkaTopic = kafkaTopic;
        this.objectMapper = objectMapper;
        this.auditMode = AuditMode.valueOf(mode.toUpperCase());
        this.kafkaTemplate = kafkaTemplate;
        log.info("Audit aspect initialized in {} mode", this.auditMode);
    }

    @Pointcut("@annotation(WeylandWatchingYou)")
    public void auditPointcut() {}

    @Around("auditPointcut()")
    public Object auditAdvice(ProceedingJoinPoint pjp) throws Throwable {
        AuditContext ctx = captureBefore(pjp);
        Object result = proceed(pjp, ctx.methodName);
        String payload = buildPayload(ctx, result);
        dispatch(payload);
        return result;
    }

    private AuditContext captureBefore(ProceedingJoinPoint pjp) {
        return new AuditContext(
                pjp.getSignature().toShortString(),
                pjp.getArgs(),
                Instant.now()
        );
    }

    private Object proceed(ProceedingJoinPoint pjp, String methodName) throws Throwable {
        try {
            return pjp.proceed();
        } catch (Throwable t) {
            log.warn("Exception in audited method {}: {}", methodName, t.getMessage());
            throw t;
        }
    }

    private String buildPayload(AuditContext ctx, Object result) {
        Map<String, Object> record = new HashMap<>();
        record.put("method", ctx.methodName);
        record.put("timestampBefore", ctx.tsBefore.toString());
        record.put("timestampAfter", Instant.now().toString());
        record.put("arguments", ctx.args);
        record.put("result", result);

        try {
            return objectMapper.writeValueAsString(record);
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize audit record for method {}", ctx.methodName, e);
            return "{\"error\":\"failed to serialize audit record\"}";
        }
    }

    private void dispatch(String payload) {
        if (auditMode == AuditMode.KAFKA) {
            sendToKafka(payload);
        } else {
            log.info("AUDIT: {}", payload);
        }
    }

    private void sendToKafka(String payload) {
        if (kafkaTemplate == null) {
            log.error("KafkaTemplate is not available, cannot send audit record");
            return;
        }
        try {
            kafkaTemplate.send(kafkaTopic, payload);
        } catch (Exception e) {
            log.error("Failed to send audit record to Kafka", e);
        }
    }

    private record AuditContext(String methodName, Object[] args, Instant tsBefore) {
    }
}
