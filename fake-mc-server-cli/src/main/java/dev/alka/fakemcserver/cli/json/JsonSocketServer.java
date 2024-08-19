package dev.alka.fakemcserver.cli.json;

public class JsonSocketServer {

    private String host;
    private int port;

    public JsonSocketServer() { }

    public JsonSocketServer(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }
}
