package org.ejatohvee.synthetichumancorestarter;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@SpringBootApplication
@EnableAspectJAutoProxy(proxyTargetClass = true)
public class SyntheticHumanCoreStarterApplication {

    public static void main(String[] args) {
        SpringApplication.run(SyntheticHumanCoreStarterApplication.class, args);
    }
}
