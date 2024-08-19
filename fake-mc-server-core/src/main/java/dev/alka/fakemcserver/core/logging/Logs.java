package dev.alka.fakemcserver.core.logging;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.*;

public class Logs {

    private static Logger logger;
    private static String whatToLog = "ping,kick,motd";

    private static boolean journalEnabled = true;
    private static final List<LogEntry> journal = new ArrayList<>();

    public static void initConsoleLogger() {
        if (logger == null) {
            logger = Logger.getLogger(Logs.class.getName());
            logger.setUseParentHandlers(false);

            ConsoleHandler consoleHandler = new ConsoleHandler();
            consoleHandler.setLevel(Level.ALL);
            consoleHandler.setFormatter(new DefaultFormatter());

            logger.addHandler(consoleHandler);
        }
    }

    public static Logger getLogger() {
        return logger;
    }

    public static void setJournal(boolean journalEnabled) {
        Logs.journalEnabled = journalEnabled;
    }

    public static boolean isJournalEnabled() {
        return journalEnabled;
    }

    public static List<LogEntry> getJournal() {
        return journal;
    }

    public static void defineWhatToLog(String whatToLog) {
        Logs.whatToLog = whatToLog.toLowerCase().replaceAll(" ", "");
    }

    public static String getWhatToLog() {
        return Logs.whatToLog;
    }

    public static boolean isLogging(String name) {
        if (whatToLog.contains("none"))
            return false;

        return whatToLog.contains(name.toLowerCase());
    }

    public static void initFileLogger(String pattern) {
        try {
            FileHandler fileHandler = new FileHandler(pattern, true);
            fileHandler.setFormatter(new DefaultFormatter());

            logger.setUseParentHandlers(false);
            logger.setLevel(Level.ALL);
            logger.addHandler(fileHandler);
        } catch (IOException exception) {
            if (logger != null) {
                logger.log(Level.SEVERE, "Error while configuring logger", exception);
            }
        }
    }

    public static void log(Level level, String message, Throwable throwable) {
        if (logger != null) {
            logger.log(level, message, throwable);
        }

        if (journalEnabled) {
            LogEntry logEntry = new LogEntry();
            logEntry.setTimestamp(new Timestamp(System.currentTimeMillis()));
            logEntry.setLevel(level);
            logEntry.setMessage(message);
            logEntry.setThrowable(throwable);
            journal.add(logEntry);
        }
    }

    public static void info(String message) {
        log(Level.INFO, message, null);
    }

    public static void warning(String message) {
        log(Level.WARNING, message, null);
    }

    public static void severe(String message) {
        log(Level.SEVERE, message, null);
    }

    public static void severe(String message, Throwable throwable) {
        log(Level.SEVERE, message, throwable);
    }

    public static void debug(String message) {
        if (isLogging("debug")) info("[DEBUG] " + message);
    }

}