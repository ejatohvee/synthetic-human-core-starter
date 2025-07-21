# Synthetic Core Starter

Lightweight Spring Boot starter for asynchronous command execution, metrics and audit (console or Kafka).

## Installation

Add the dependency to your **application**’s `pom.xml` (or `build.gradle`):

```xml
<dependency>
  <groupId>com.weyland</groupId>
  <artifactId>synthetic-core-starter</artifactId>
  <version>1.0.0</version>
</dependency>
```

In your `src/main/resources/application.yml`:
```xml
spring:
  application:
    name: synthetic-core-starter

management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics

# Thread‑pool and queue
synthetic:
  core:
    thread-pool:
      size: 8          # number of worker threads
    queue:
      capacity: 200    # max queued COMMON tasks

# Audit settings
  audit:
    enabled: true      # turn audit on/off
    mode: console      # or kafka
    kafka-topic: synthetic-audit

spring:
  kafka:
    bootstrap-servers: localhost:9092
```

## Invoke from your code
```java
@Component
public class MyService {
  private final CommandExecutor immediateExecutor;
  private final CommandExecutor queuedExecutor;

  public MyService(
    @Qualifier("immediateCommandExecutor") CommandExecutor immediate,
    @Qualifier("queuedCommandExecutor") CommandExecutor queued
  ) {
    this.immediateExecutor = immediate;
    this.queuedExecutor = queued;
  }

  public void submit(Command cmd) {
    if (cmd.getPriority() == CRITICAL) {
      immediateExecutor.execute(cmd);
    } else {
      queuedExecutor.execute(cmd);
    }
  }
}
```

## Available metrics
```
GET /actuator/metrics/android.queue.size
GET /actuator/metrics/android.tasks.completed
```

Annotate any method you want to audit:
```java
@WeylandWatchingYou
public void process(Command cmd) { … }
```
**console mode**: writes JSON audit records to your log

**kafka mode**: sends JSON to `${synthetic.audit.kafka-topic}` via KafkaTemplate
