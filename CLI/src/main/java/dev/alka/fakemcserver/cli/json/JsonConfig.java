package dev.alka.fakemcserver.cli.json;

import com.google.gson.annotations.SerializedName;

public class JsonConfig {

    @SerializedName("socket_server")
    private JsonSocketServer socketServer;
    @SerializedName("minecraft_server")
    private JsonFakeServer fakeServer;

    public JsonConfig() { }

    public JsonConfig(JsonSocketServer socketServer, JsonFakeServer fakeServer) {
        this.socketServer = socketServer;
        this.fakeServer = fakeServer;
    }

    public JsonSocketServer getSocketServer() {
        return socketServer;
    }

    public void setSocketServer(JsonSocketServer socketServer) {
        this.socketServer = socketServer;
    }

    public JsonFakeServer getFakeServer() {
        return fakeServer;
    }

    public void setFakeServer(JsonFakeServer fakeServer) {
        this.fakeServer = fakeServer;
    }
}