package org.ejatohvee.synthetichumancorestarter.services;

import org.ejatohvee.synthetichumancorestarter.aspect.WeylandWatchingYou;
import org.ejatohvee.synthetichumancorestarter.models.Command;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;


@Component
public class ImmediateCommandExecutor implements CommandExecutor {
    private static final Logger logger = LoggerFactory.getLogger(ImmediateCommandExecutor.class);
    private final ExecutorService executor;
    private final CommandQueueService commandQueueService;

    public ImmediateCommandExecutor(@Qualifier("commandExecutorService") ExecutorService executor, CommandQueueService commandQueueService) {
        this.executor = executor;
        this.commandQueueService = commandQueueService;
    }

    @WeylandWatchingYou
    @Override
    public void execute(Command command) {
        Future<?> future = executor.submit(() -> {
            try {
                logger.info("Executing CRITICAL command: {}", command.getDescription());

                commandQueueService.markTaskCompleted(command.getAuthor());
            } catch (Exception ex) {
                logger.error("Error executing CRITICAL command: {}", command.getDescription(), ex);
                throw ex;
            }
        });

        logger.debug("Submitted CRITICAL command for async execution: {}", future);
    }
}
