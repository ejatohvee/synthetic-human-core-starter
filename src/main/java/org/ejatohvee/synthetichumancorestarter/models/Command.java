package org.ejatohvee.synthetichumancorestarter.models;


public class Command {
    private final String description;

    private final Priority priority;

    private final String author;

    private final String time;

    public Command(String description, Priority priority, String author, String time) {
        this.description = description;
        this.priority = priority;
        this.author = author;
        this.time = time;
    }

    public String getDescription() {
        return description;
    }

    public Priority getPriority() {
        return priority;
    }

    public String getAuthor() {
        return author;
    }

    public String getTime() {
        return time;
    }

}
