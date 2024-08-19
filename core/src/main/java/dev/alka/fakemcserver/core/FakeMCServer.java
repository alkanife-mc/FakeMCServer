package dev.alka.fakemcserver.core;

import dev.alka.fakemcserver.core.logging.Logs;

import java.io.Closeable;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.List;

/**
 * @author michidk
 */
public class FakeMCServer {

    private volatile boolean stopping = false;

    public final int SOCKET_BACKLOG = 5;
    public volatile ServerSocket server = null;

    private volatile String socketHost = "0.0.0.0";
    private volatile int socketPort = 25565;
    private volatile String fakeServerIp = null;
    private volatile String base64Icon = null;
    private volatile boolean allowAllClients = true;
    private volatile List<Integer> allowedProtocols = new ArrayList<>();
    private volatile String versionName = "Minecraft";
    private volatile String motd = "A Minecraft server";
    private volatile List<String> players = new ArrayList<>();
    private volatile int connectedPlayers = 0;
    private volatile int maxPlayers = 0;
    private volatile String kickMessage = "Disconnected";

    public FakeMCServer() { }

    public void startServer() {
        if (stopping)
            throw new IllegalAccessError();

        Logs.info("Starting server...");

        final String host = (getSocketHost() == null || getSocketHost().isEmpty()) ? "0.0.0.0" : getSocketHost();
        final int port = (getSocketPort() == 0) ? 25565 : getSocketPort();

        try {
            final InetAddress address = InetAddress.getByName(host);
            server = new ServerSocket(port, SOCKET_BACKLOG, address);
            Logs.info("ServerSocket started on " + host + ":" + port);

            if (getFakeServerIp() != null)
                Logs.info("Minecraft IP: " + getFakeServerIp());

            while (!server.isClosed()) {
                ResponderThread responderThread = new ResponderThread(this, server.accept());
                responderThread.setName("Responder Thread");
                responderThread.start();
            }
        } catch (Exception exception) {
            if (!stopping) {
                Logs.severe("Failed to start the socket server on " + host + ":" + port, exception);
                System.exit(0);
            }
        } finally {
            stopServer();
        }
    }

    public void stopServer() {
        Logs.info("Stopping server");

        stopping = true;

        if (server == null)
            return;

        safeClose(server);
        server = null;
    }

    public void safeClose(final Closeable obj) {
        if (obj == null)
            return;

        try {
            obj.close();
        } catch (Exception ignore) {}
    }

    public String getSocketHost() {
        return socketHost;
    }

    public void setSocketHost(String socketHost) {
        this.socketHost = socketHost;
    }

    public int getSocketPort() {
        return socketPort;
    }

    public void setSocketPort(int socketPort) {
        this.socketPort = socketPort;
    }

    public String getFakeServerIp() {
        return fakeServerIp;
    }

    public void setFakeServerIp(String fakeServerIp) {
        this.fakeServerIp = fakeServerIp;
    }

    public String getBase64Icon() {
        return base64Icon;
    }

    public void setBase64Icon(String base64Icon) {
        this.base64Icon = base64Icon;
    }

    public List<Integer> getAllowedProtocols() {
        return allowedProtocols;
    }

    public void setAllowedProtocols(List<Integer> allowedProtocols) {
        this.allowedProtocols = allowedProtocols;
    }

    public String getVersionName() {
        return versionName;
    }

    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }

    public String getMotd() {
        return motd;
    }

    public void setMotd(String motd) {
        this.motd = motd;
    }

    public List<String> getPlayers() {
        return players;
    }

    public void setPlayers(List<String> players) {
        this.players = players;
    }

    public int getConnectedPlayers() {
        return connectedPlayers;
    }

    public void setConnectedPlayers(int connectedPlayers) {
        this.connectedPlayers = connectedPlayers;
    }

    public int getMaxPlayers() {
        return maxPlayers;
    }

    public void setMaxPlayers(int maxPlayers) {
        this.maxPlayers = maxPlayers;
    }

    public String getKickMessage() {
        return kickMessage;
    }

    public void setKickMessage(String kickMessage) {
        this.kickMessage = kickMessage;
    }

    public boolean isAllowAllClients() {
        return allowAllClients;
    }

    public void setAllowAllClients(boolean allowAllClients) {
        this.allowAllClients = allowAllClients;
    }
}
