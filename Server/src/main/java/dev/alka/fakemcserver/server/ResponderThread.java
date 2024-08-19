package dev.alka.fakemcserver.server;

import dev.alka.fakemcserver.server.logging.Logs;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

/**
 * @author michidk
 */
public class ResponderThread extends Thread {

    private final FakeMCServer fakeMCServer;
    private volatile Thread thread = null;
    private final Socket socket;
    private final String remoteHost;
    private volatile boolean enabled = false;
    private final DataInputStream in;
    private final DataOutputStream out;

    public ResponderThread(final FakeMCServer fakeMCServer, final Socket socket) throws IOException {
        this.fakeMCServer = fakeMCServer;

        if (socket == null)
            throw new NullPointerException();

        this.socket = socket;
        this.remoteHost = socket.getRemoteSocketAddress().toString().substring(1);
        this.in = new DataInputStream(socket.getInputStream());
        this.out = new DataOutputStream(socket.getOutputStream());

        socket.setSoTimeout(3000); // 3s

        this.enabled = true;
    }

    @Override
    public void run() {
        this.thread = Thread.currentThread();
        boolean showMotd = false;
        int version = -1;
        int state = -1;

        try {
            while (this.socket.isConnected() && this.enabled) {
                final int packetLength = ByteBufUtils.readVarInt(this.in);
                final int packetId = ByteBufUtils.readVarInt(this.in);

                if (packetLength == 0)
                    return;

                // handshake
                if (packetId == 0) {
                    if (!showMotd) {
                        version = ByteBufUtils.readVarInt(this.in);
                        final String ip = ByteBufUtils.readUTF8(this.in);
                        final int port = this.in.readUnsignedShort();
                        state = ByteBufUtils.readVarInt(this.in);

                        Logs.debug("===========================");
                        Logs.debug("State request: " +
                                "length:" + packetLength + ", " +
                                "packetId:" + packetId + ", " +
                                "version:" + version + ", " +
                                "state:" + state + ", " +
                                "ip:" + ip + ", " +
                                "port:" + port);

                        if (fakeMCServer.getFakeServerIp() != null) {
                            if (!ip.equalsIgnoreCase(fakeMCServer.getFakeServerIp())) {
                                Logs.debug("State request denied, wrong Minecraft IP");
                                Logs.debug("===========================");
                                return;
                            }
                        }

                        if (state == 1) {
                            // Ping
                            showMotd = true;

                            if (Logs.isLogging("ping"))
                                Logs.info("Ping <-> " + this.remoteHost);
                        } else if (state == 2) {
                            // Login
                            String kickMessage = "{text:\"Disconnected\"}";

                            if (fakeMCServer.getKickMessage() != null)
                                kickMessage = GsonComponentSerializer.gson().serialize(MiniMessage.miniMessage().deserialize(fakeMCServer.getKickMessage()));

                            writeData(kickMessage);

                            if (Logs.isLogging("kick"))
                                Logs.info("Kick <-> " + this.remoteHost);

                            Logs.debug("Kick message: " + kickMessage);
                            Logs.debug("===========================");
                            return;
                        }
                    } else {
                        // MOTD
                        String motd = StringUtils.buildServerPingResponse(fakeMCServer, version);
                        writeData(motd);

                        if (Logs.isLogging("motd"))
                            Logs.info("MOTD <-> " + this.remoteHost);

                        Logs.debug("MOTD sent: " + motd);
                        showMotd = false;
                    }
                } else if(packetId == 1) {
                    long lng = this.in.readLong();
                    Logs.debug("Pong! lng:" + lng);
                    ByteBufUtils.writeVarInt(this.out, 9);
                    ByteBufUtils.writeVarInt(this.out, 1);
                    this.out.writeLong(lng);
                    this.out.flush();
                } else {
                    Logs.debug("Unknown packet: " + packetId);
                    return;
                }
            }
        }
        //ignore this unnecessary error
        catch (EOFException ignore) {
            Logs.debug("(end of socket)");
            Logs.debug("===========================");
        } catch (SocketTimeoutException ignore) {
            Logs.debug("(socket timeout)");
            Logs.debug("===========================");
        } catch (SocketException ignore) {
            Logs.debug("(socket closed)");
            Logs.debug("===========================");
        } catch (IOException exception) {
            Logs.severe("IOException", exception);
        } finally {
            closeSocket();
            this.thread = null;
        }
    }

    private void closeSocket() {
        this.enabled = false;
        fakeMCServer.safeClose(this.in);
        fakeMCServer.safeClose(this.out);
        fakeMCServer.safeClose(this.socket);
        if(this.thread != null)
            this.thread.interrupt();
    }

    private void writeData(final String data) throws IOException {
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        final DataOutputStream dos = new DataOutputStream(baos);
        ByteBufUtils.writeVarInt(dos, 0);
        ByteBufUtils.writeUTF8(dos, data);
        ByteBufUtils.writeVarInt(this.out, baos.size());
        this.out.write(baos.toByteArray());
        this.out.flush();
    }
}
