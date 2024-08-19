package dev.alka.fakemcserver.cli.json;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class JsonFakeServer {

    @SerializedName("server_ip")
    private String serverIP;
    private JsonProtocol protocol;
    @SerializedName("version_name")
    private String versionName;
    private String motd;
    private List<String> players;
    @SerializedName("connected_players")
    private int connectedPlayers;
    private int slots;
    @SerializedName("kick_message")
    private String kickMessage;
    @SerializedName("server_icon")
    private String serverIcon;

    public JsonFakeServer() { }

    public JsonFakeServer(String serverIP, JsonProtocol protocol, String versionName, String motd, List<String> players, int connectedPlayers, int slots, String kickMessage, String serverIcon) {
        this.serverIP = serverIP;
        this.protocol = protocol;
        this.versionName = versionName;
        this.motd = motd;
        this.players = players;
        this.connectedPlayers = connectedPlayers;
        this.slots = slots;
        this.kickMessage = kickMessage;
        this.serverIcon = serverIcon;
    }

    public String getServerIP() {
        return serverIP;
    }

    public void setServerIP(String serverIP) {
        this.serverIP = serverIP;
    }

    public JsonProtocol getProtocol() {
        return protocol;
    }

    public void setProtocol(JsonProtocol protocol) {
        this.protocol = protocol;
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

    public int getSlots() {
        return slots;
    }

    public void setSlots(int slots) {
        this.slots = slots;
    }

    public String getKickMessage() {
        return kickMessage;
    }

    public void setKickMessage(String kickMessage) {
        this.kickMessage = kickMessage;
    }

    public String getServerIcon() {
        return serverIcon;
    }

    public void setServerIcon(String serverIcon) {
        this.serverIcon = serverIcon;
    }
}
