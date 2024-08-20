package dev.alka.fakemcserver.cli.json;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class JsonProtocol {

    @SerializedName("allow_all_clients")
    private boolean allowAllClients;
    @SerializedName("versions")
    private List<Integer> protocols;

    public JsonProtocol() { }

    public JsonProtocol(boolean allowAllClients, List<Integer> protocols) {
        this.allowAllClients = allowAllClients;
        this.protocols = protocols;
    }

    public boolean isAllowAllClients() {
        return allowAllClients;
    }

    public void setAllowAllClients(boolean allowAllClients) {
        this.allowAllClients = allowAllClients;
    }

    public List<Integer> getProtocols() {
        return protocols;
    }

    public void setProtocols(List<Integer> protocols) {
        this.protocols = protocols;
    }
}