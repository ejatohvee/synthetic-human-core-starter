package org.ejatohvee.synthetichumancorestarter.services;

import org.ejatohvee.synthetichumancorestarter.models.Command;

public interface CommandExecutor {
    void execute(Command command);
}
