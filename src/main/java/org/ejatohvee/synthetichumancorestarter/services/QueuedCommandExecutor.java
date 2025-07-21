package org.ejatohvee.synthetichumancorestarter.services;

import org.ejatohvee.synthetichumancorestarter.aspect.WeylandWatchingYou;
import org.ejatohvee.synthetichumancorestarter.exceptions.QueueFullException;
import org.ejatohvee.synthetichumancorestarter.models.Command;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutorService;

@Component
public class QueuedCommandExecutor implements CommandExecutor {
    private static final Logger logger = LoggerFactory.getLogger(QueuedCommandExecutor.class);
    private final ExecutorService executor;
    private final CommandQueueService commandQueueService;


    public QueuedCommandExecutor(@Qualifier("commandExecutorService") ExecutorService executor, CommandQueueService commandQueueService) {
        this.executor = executor;
        this.commandQueueService = commandQueueService;
    }

    @WeylandWatchingYou
    @Override
    public void execute(Command command) {
        try {
            executor.execute(() ->
                    logger.info("Processing COMMON command: {}", command.getDescription()));

                    commandQueueService.markTaskCompleted(command.getAuthor());
        } catch (RuntimeException ex) {
            if (ex.getCause() instanceof QueueFullException
             || ex instanceof QueueFullException) {
                throw new QueueFullException("Cannot enqueue COMMON command, queue is full");
            }

            throw ex;
        }
    }
}
