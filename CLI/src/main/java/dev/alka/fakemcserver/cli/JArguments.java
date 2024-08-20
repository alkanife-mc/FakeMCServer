package dev.alka.fakemcserver.cli;

import com.beust.jcommander.Parameter;

public class JArguments {

    @Parameter(
            names = { "-help", "-h" },
            description = "Display usage",
            help = true,
            order = 1
    )
    private boolean help = false;

    @Parameter(
            names = { "-version", "-v" },
            description = "Display version",
            order = 2
    )
    private boolean version = false;

    @Parameter(
            names = { "-start" },
            description = "Start server",
            help = true,
            order = 3
    )
    private boolean start = false;

    @Parameter(
            names = { "-logs" },
            description = "What you want to log, separated by a comma. Values: NONE, DEBUG, PING, KICK, MOTD. (default: 'PING,KICK')",
            order = 4
    )
    private String logs = "PING,KICK";

    @Parameter(
            names = { "-log-file" },
            description = "Log file pattern (example: server.log). By default no log file is generated.",
            order = 5
    )
    private String logFilePattern = null;

    @Parameter(
            names = { "-config" },
            description = "Configuration path (default: 'config.json')",
            order = 6
    )
    private String configurationPath = "config.json";

    @Parameter(
            names = { "-config-override" },
            description = "Skip the configuration file, and override with raw JSON",
            order = 7
    )
    private String configurationOverride = null;

    public JArguments() { }

    public boolean isHelp() {
        return help;
    }

    public void setHelp(boolean help) {
        this.help = help;
    }

    public boolean isVersion() {
        return version;
    }

    public void setVersion(boolean version) {
        this.version = version;
    }

    public boolean isStart() {
        return start;
    }

    public void setStart(boolean start) {
        this.start = start;
    }

    public String getWhatToLog() {
        return logs;
    }

    public void setLogs(String logs) {
        this.logs = logs;
    }

    public String getLogFilePattern() {
        return logFilePattern;
    }

    public void setLogFilePattern(String logFilePattern) {
        this.logFilePattern = logFilePattern;
    }

    public String getConfigurationPath() {
        return configurationPath;
    }

    public void setConfigurationPath(String configurationPath) {
        this.configurationPath = configurationPath;
    }

    public String getConfigurationOverride() {
        return configurationOverride;
    }

    public void setConfigurationOverride(String configurationOverride) {
        this.configurationOverride = configurationOverride;
    }
}