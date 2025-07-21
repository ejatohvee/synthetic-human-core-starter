package org.ejatohvee.synthetichumancorestarter.exceptions;

public class QueueFullException extends RuntimeException {
    public QueueFullException(String message) {
        super(message);
    }
}
