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

    public static final String VERSION = "1.1";
    public static final String URL = "https://github.com/alkanife/FakeMCServer";

    private static Logger logger;

    public static volatile boolean debug = false;
    private static volatile boolean stopping = false;

    public static final int SOCKET_BACKLOG = 5;

    private static JSONConfiguration configuration;

    public static volatile ServerSocket server = null;

    public static void main(final String[] args) {
        System.out.println("FakeMCServer version " + VERSION);
        System.out.println(URL);
        System.out.println("Original version by xxmicloxx, lorenzop and michidk");
        System.out.println("Use the 'debug' argument to enter debug mode");

        if (args.length > 0) {
            if (args[0].equalsIgnoreCase("help"))
                return;

            if (args[0].equalsIgnoreCase("debug"))
                debug = true;
        }

        System.out.println("----------------------------------------------------");

        logger = LoggerFactory.getLogger(FakeMCServer.class);

        debug("Debug mode enabled");

        try {
            loadConfiguration();
            addShutdownHook();
            startServer();
        } catch (Exception exception) {
            System.exit(0);
        }
    }

    public static void loadConfiguration() throws Exception {
        getLogger().info("Loading configuration...");

        File confFile = new File("config.json");

        if (!confFile.exists())
            throw new FileNotFoundException("Configuration file not found.");

        String content = Files.readString(confFile.toPath());

        Gson gson = new GsonBuilder()
                .serializeNulls()
                .create();

        configuration = gson.fromJson(content, JSONConfiguration.class);
    }

    public static void addShutdownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread(FakeMCServer::stopServer));
    }

    public static void startServer() {
        if (stopping)
            throw new IllegalAccessError();

        getLogger().info("Starting server...");

        final String host = (getConfig().getHost() == null || getConfig().getHost().isEmpty()) ? "127.0.0.1" : getConfig().getHost();
        final int port = (getConfig().getPort() == 0) ? 25565 : getConfig().getPort();

        try {
            final InetAddress address = InetAddress.getByName(host);
            server = new ServerSocket(port, SOCKET_BACKLOG, address);
            getLogger().info("Server started on " + host + ":" + port);

            while (!server.isClosed())
                new ResponderThread(server.accept()).start();
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

    public static void stopServer() {
        stopping = true;

        if (server == null)
            return;

        getLogger().info("Stopping server...");
        safeClose(server);
        server = null;
        getLogger().info("Server stopped, bye!");
    }

    public static void debug(final String message) {
        if (!debug)
            return;

        if (logger == null)
            System.out.println("[DEBUG] " + message);
        else
            getLogger().info("[DEBUG] " + message);
    }

    public static void safeClose(final Closeable obj) {
        if (obj == null)
            return;

        try {
            obj.close();
        } catch (Exception ignore) {}
    }

    public static JSONConfiguration getConfig() {
        return configuration;
    }

    public static Logger getLogger() {
        return logger;
    }
}
