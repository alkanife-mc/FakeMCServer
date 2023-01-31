package me.michidik.fakemcserver;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;

/**
 * @author michidk
 */

public class ResponderThread extends Thread {

    private volatile Thread thread = null;
    private final Socket socket;
    private final String remoteHost;
    private volatile boolean enabled = false;
    private final DataInputStream in;
    private final DataOutputStream out;

    public ResponderThread(final Socket socket) throws IOException {
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

        try {
            while (this.socket.isConnected() && this.enabled) {
                final int length   = ByteBufUtils.readVarInt(this.in);
                final int packetId = ByteBufUtils.readVarInt(this.in);

                if (length == 0)
                    return;

                // handshake
                if (packetId == 0) {
                    if (!showMotd) {
                        final int version = ByteBufUtils.readVarInt(this.in);
                        final String ip = ByteBufUtils.readUTF8(this.in);
                        final int port = this.in.readUnsignedShort();
                        final int state = ByteBufUtils.readVarInt(this.in);

                        FakeMCServer.debug("State request: " +
                                "len:" + length + " " +
                                "id:" + packetId + " " +
                                "vers:" + version + " " +
                                "state:" + state + " " +
                                "ip:" + ip + " " +
                                "port:" + port);

                        if (state == 1) {
                            // Ping
                            showMotd = true;
                            FakeMCServer.getLogger().info("Ping <-> " + this.remoteHost);
                        } else if (state == 2) {
                            // Login
                            String kickMessage = StringUtils.parse(StringUtils.combineList(FakeMCServer.getConfig().getKick_message()));
                            writeData("{text:\"" + kickMessage + "\"}");
                            FakeMCServer.getLogger().info("Kick <-> " + this.remoteHost);
                            FakeMCServer.debug("Kick message: " + kickMessage);
                            return;
                        }
                    } else {
                        // MOTD packet
                        String motd = StringUtils.buildServerPingResponse();
                        writeData(motd);
                        FakeMCServer.getLogger().info("MOTD <-> " + this.remoteHost);
                        FakeMCServer.debug("MOTD: " + motd);
                        showMotd = false;
                    }
                } else if(packetId == 1) {
                    long lng = this.in.readLong();
                    FakeMCServer.debug("Pong! lng:" + lng);
                    ByteBufUtils.writeVarInt(this.out, 9);
                    ByteBufUtils.writeVarInt(this.out, 1);
                    this.out.writeLong(lng);
                    this.out.flush();
                } else {
                    FakeMCServer.getLogger().warn("Unknown packet: " + packetId);
                    return;
                }
            }
        }
        //ignore this unnecessary error
        catch (EOFException ignore) {
            FakeMCServer.debug("(end of socket)");
        } catch (SocketTimeoutException ignore) {
            FakeMCServer.debug("(socket timeout)");
        } catch (SocketException ignore) {
            FakeMCServer.debug("(socket closed)");
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            closeSocket();
            this.thread = null;
        }
    }

    private final void closeSocket() {
        this.enabled = false;
        FakeMCServer.safeClose(this.in);
        FakeMCServer.safeClose(this.out);
        FakeMCServer.safeClose(this.socket);
        if(this.thread != null)
            this.thread.interrupt();
    }

    private void writeData(final String data) throws IOException {
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        final DataOutputStream      dos  = new DataOutputStream(baos);
        ByteBufUtils.writeVarInt(dos, 0);
        ByteBufUtils.writeUTF8(dos, data);
        ByteBufUtils.writeVarInt(this.out, baos.size());
        this.out.write(baos.toByteArray());
        this.out.flush();
    }
}
