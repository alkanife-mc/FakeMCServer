package me.michidik.fakemcserver;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.io.File;
import java.io.FileNotFoundException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.nio.file.Files;

/**
 * @author michidk
 */
public class FakeMCServer {

    private Logger logger;
    private volatile boolean debug;
    private volatile boolean verbose = false;
    private volatile boolean stopping = false;
    public final int SOCKET_BACKLOG = 5;
    private FakeMCServerConfiguration configuration;
    public volatile ServerSocket server = null;

    public FakeMCServer() {}

    public FakeMCServer(Logger logger) {
        this.logger = logger;
    }

    public void setupDefaultLogger() {
        logger = LoggerFactory.getLogger(FakeMCServer.class);
        logger.info("Using default logger");
    }

    public void loadConfiguration(String configPath) throws Exception {
        getLogger().info("Loading configuration...");

        File confFile = new File(configPath);

        if (!confFile.exists())
            throw new FileNotFoundException("Configuration file not found");

        String content = Files.readString(confFile.toPath());

        Gson gson = new GsonBuilder()
                .serializeNulls()
                .create();

        configuration = gson.fromJson(content, FakeMCServerConfiguration.class);
    }

    public void addShutdownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread(this::stopServer, "Shutdown hook"));
    }

    public void startServer() {
        if (stopping)
            throw new IllegalAccessError();

        getLogger().info("Starting server...");

        final String host = (getConfig().getHost() == null || getConfig().getHost().isEmpty()) ? "127.0.0.1" : getConfig().getHost();
        final int port = (getConfig().getPort() == 0) ? 25565 : getConfig().getPort();

        try {
            final InetAddress address = InetAddress.getByName(host);
            server = new ServerSocket(port, SOCKET_BACKLOG, address);
            getLogger().info("ServerSocket started on " + host + ":" + port);

            while (!server.isClosed()) {
                ResponderThread responderThread = new ResponderThread(this, server.accept());
                responderThread.setName("Responder Thread");
                responderThread.start();
            }
        } catch (Exception exception) {
            if (!stopping) {
                getLogger().error("Failed to start the server on " + host + ":" + port);
                exception.printStackTrace();
                System.exit(0);
            }
        } finally {
            stopServer();
        }
    }

    public void stopServer() {
        stopping = true;

        if (server == null)
            return;

        getLogger().info("Stopping server...");
        safeClose(server);
        server = null;
        getLogger().info("Server stopped, bye!");
    }

    public void debug(final String message) {
        if (!debug)
            return;

        if (logger == null)
            System.out.println("[DEBUG] " + message);
        else
            getLogger().info("[DEBUG] " + message);
    }

    public void verbose(final String message) {
        if (verbose)
            getLogger().info(message);
    }

    public void safeClose(final Closeable obj) {
        if (obj == null)
            return;

        try {
            obj.close();
        } catch (Exception ignore) {}
    }

    public FakeMCServerConfiguration getConfig() {
        return configuration;
    }

    public void setConfiguration(FakeMCServerConfiguration configuration) {
        this.configuration = configuration;
    }

    public boolean isVerbose() {
        return verbose;
    }

    public void setVerbose(boolean verbose) {
        this.verbose = verbose;
    }

    public Logger getLogger() {
        return logger;
    }

    public void setLogger(Logger logger) {
        this.logger = logger;
    }

    public void setDebug(boolean debug) {
        this.debug = debug;
    }
}
