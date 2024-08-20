package dev.alka.fakemcserver;

import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;

import java.util.UUID;
import java.util.regex.Pattern;

public class StringUtils {

    public static String parse(final String msg) {
        Pattern colorPattern = Pattern.compile("&([0-9a-fk-or])");

        if (msg == null)
            return null;

        if (msg.isEmpty())
            return "";

        return colorPattern.matcher(msg).replaceAll("\u00a7$1");
    }

    public static String buildServerPingResponse(FakeMCServer server, int clientVersion) {
        StringBuilder sb = new StringBuilder();

        // Version name
        String versionName = "Minecraft";
        if (server.getVersionName() != null)
            if (!server.getVersionName().isEmpty())
                versionName = StringUtils.parse(server.getVersionName());
        sb.append("{\"version\":{\"name\":\"").append(versionName).append("\",");

        // Protocol
        sb.append("\"protocol\":");

        if (server.isAllowAllClients()) {
            sb.append(clientVersion);
        } else {
            if (server.getAllowedProtocols() == null) {
                sb.append(0);
            } else {
                boolean allowClient = false;

                for (int allowedProtocol : server.getAllowedProtocols()) {
                    if (allowedProtocol == clientVersion) {
                        allowClient = true;
                        break;
                    }
                }

                if (allowClient) {
                    sb.append(clientVersion);
                } else {
                    sb.append(0);
                }
            }
        }

        sb.append("},");

        // Max players
        sb.append("\"players\":{\"max\":").append(server.getMaxPlayers()).append(",");

        // Online players
        sb.append("\"online\":").append(server.getConnectedPlayers()).append(",");

        // Player sample
        sb.append("\"sample\":[");
        if (server.getPlayers() != null) {
            if (!server.getPlayers().isEmpty()) {
                int count = 0;

                for (final String player : server.getPlayers()) {
                    if (count != 0)
                        sb.append(",");

                    count++;
                    sb.append("{\"name\":\"").append(StringUtils.parse(player)).append("\",\"id\":\"").append(UUID.randomUUID()).append("\"}");

                    if (count == 10 && server.getPlayers().size() > 10)
                        break;
                }
            }
        }
        sb.append("]},");

        // MOTD
        String motd = "{\"text\":\"A Minecraft Server\"}";

        if (server.getMotd() != null)
            motd = GsonComponentSerializer.gson().serialize(MiniMessage.miniMessage().deserialize(server.getMotd()));

        sb.append("\"description\":").append(motd).append(",");

        // Icon
        sb.append("\"favicon\": \"").append(server.getBase64Icon() == null ? "" : server.getBase64Icon()).append("\" }");

        return sb.toString();
    }
}