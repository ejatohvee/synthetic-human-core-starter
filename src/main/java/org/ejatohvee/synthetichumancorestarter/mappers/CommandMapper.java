package org.ejatohvee.synthetichumancorestarter.mappers;


import org.ejatohvee.synthetichumancorestarter.models.Command;
import org.ejatohvee.synthetichumancorestarter.models.CommandRequest;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class CommandMapper {
    public Command toDomain(CommandRequest req) {
        return new Command(
                req.description(),
                req.priority(),
                req.author(),
                Instant.parse(req.time()).toString()
        );
    }
}
