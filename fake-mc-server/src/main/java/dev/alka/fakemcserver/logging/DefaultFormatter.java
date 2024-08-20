package dev.alka.fakemcserver.logging;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

public class DefaultFormatter extends Formatter {

    @Override
    public String format(LogRecord record) {
        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append(String.format("[%1$td-%1$tm-%1$tY %1$tT] [%2$s] %3$s%n",
                record.getMillis(),
                record.getLevel().getLocalizedName(),
                formatMessage(record)));

        if (record.getThrown() != null) {
            try (StringWriter stringWriter = new StringWriter();
                 PrintWriter printWriter = new PrintWriter(stringWriter)) {
                record.getThrown().printStackTrace(printWriter);
                stringBuilder.append(stringWriter.toString());
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }

        return stringBuilder.toString();
    }
}