package dev.alka.fakemcserver.logging;

import java.sql.Timestamp;
import java.util.logging.Level;

public class LogEntry {

    private Timestamp timestamp;
    private Level level;
    private String message;
    private Throwable throwable;

    public LogEntry() { }

    public LogEntry(Timestamp timestamp, Level level, String message, Throwable throwable) {
        this.timestamp = timestamp;
        this.level = level;
        this.message = message;
        this.throwable = throwable;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public Level getLevel() {
        return level;
    }

    public void setLevel(Level level) {
        this.level = level;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Throwable getThrowable() {
        return throwable;
    }

    public void setThrowable(Throwable throwable) {
        this.throwable = throwable;
    }
}