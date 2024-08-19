package dev.alka.fakemcserver.cli;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParameterException;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dev.alka.fakemcserver.cli.json.JsonConfig;
import dev.alka.fakemcserver.cli.json.JsonFakeServer;
import dev.alka.fakemcserver.cli.json.JsonProtocol;
import dev.alka.fakemcserver.cli.json.JsonSocketServer;
import dev.alka.fakemcserver.server.FakeMCServer;
import dev.alka.fakemcserver.server.logging.Logs;
import dev.alka.utils.builds.BuildMeta;
import dev.alka.utils.builds.BuildUtils;
import dev.alka.utils.cli.PrettyUsage;
import dev.alka.utils.jcommander.JCommandArranger;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.Files;
import java.sql.Array;
import java.util.ArrayList;
import java.util.Base64;

public class FakeMCServerCLI {

    private final JArguments jArguments;
    private final JCommander jCommander;

    public FakeMCServerCLI(String[] args) {
        jArguments = new JArguments();
        jCommander = JCommander.newBuilder().programName("FakeMCServer").addObject(jArguments).build();

        try {
            jCommander.parse(args);
        } catch (ParameterException exception) {
            System.out.println("Invalid arguments, see correct usage with '-help'");
            return;
        }

        if (jArguments.isHelp()) {
            printUsage();
            return;
        }

        if (!jArguments.isStart() && !jArguments.isVersion()) {
            System.out.println("No '-start' flag was found! Printing usage");
            System.out.println();
            printUsage();
            return;
        }

        BuildMeta buildMeta = BuildUtils.readBuild("fakemcserver-cli");

        if (jArguments.isVersion()) {
            System.out.println("FakeMCServer version " + buildMeta.getVersionAndBuildTime());
            System.out.println("Git: " + buildMeta.getGitRevision());
            return;
        }

        // Handle logging
        Logs.setJournal(false);
        Logs.initConsoleLogger();
        Logs.defineWhatToLog(jArguments.getWhatToLog());

        if (jArguments.getLogFilePattern() != null)
            Logs.initFileLogger(jArguments.getLogFilePattern());

        // Splash text
        Logs.info("FakeMCServer version " + buildMeta.getVersionAndBuildTime());
        Logs.info("Forked by Alkanife @ https://alka.dev");
        Logs.info("GitHub link: https://github.com/alkanife/fake-mc-server");
        Logs.info("Original creators: xxmicloxx, lorenzop and michidk");
        Logs.info("-------------------------------------");

        // Inform logging
        String[] logs = jArguments.getWhatToLog().split(",");
        String whatToLog = "Logging: ";
        boolean first = true;
        for (String log : logs) {
            if (!first) whatToLog += ", ";
            else first = false;

            whatToLog += log.toLowerCase();
        }

        Logs.info(whatToLog);

        // Configure FakeMCServer
        FakeMCServer fakeMCServer = new FakeMCServer();

        if (jArguments.getConfigurationOverride() == null)
            Logs.info("Using configuration at path '" + jArguments.getConfigurationPath() + "'");
        else
            Logs.info("Overriding configuration!");

        JsonConfig jsonConfig = handleConfig(fakeMCServer);

        if (jsonConfig == null)
            return;

        if (jsonConfig.getSocketServer().getHost() != null)
            fakeMCServer.setSocketHost(jsonConfig.getSocketServer().getHost());

        if (jsonConfig.getSocketServer().getPort() > 0)
            fakeMCServer.setSocketPort(jsonConfig.getSocketServer().getPort());

        if (jsonConfig.getFakeServer().getServerIP() != null)
            fakeMCServer.setFakeServerIp(jsonConfig.getFakeServer().getServerIP());

        fakeMCServer.setAllowAllClients(jsonConfig.getFakeServer().getProtocol().isAllowAllClients());
        fakeMCServer.setAllowedProtocols(jsonConfig.getFakeServer().getProtocol().getProtocols());

        if (jsonConfig.getFakeServer().getVersionName() != null)
            fakeMCServer.setVersionName(jsonConfig.getFakeServer().getVersionName());

        if (jsonConfig.getFakeServer().getMotd() != null)
            fakeMCServer.setMotd(jsonConfig.getFakeServer().getMotd());

        if (jsonConfig.getFakeServer().getPlayers() != null)
            fakeMCServer.setPlayers(jsonConfig.getFakeServer().getPlayers());

        fakeMCServer.setConnectedPlayers(jsonConfig.getFakeServer().getConnectedPlayers());
        fakeMCServer.setMaxPlayers(jsonConfig.getFakeServer().getSlots());

        if (jsonConfig.getFakeServer().getKickMessage() != null)
            fakeMCServer.setKickMessage(jsonConfig.getFakeServer().getKickMessage());

        try {
            fakeMCServer.setBase64Icon(getBase64img(jsonConfig.getFakeServer().getServerIcon()));
        } catch (Exception exception) {
            Logs.severe("An error occurred while manipulating the server icon!", exception);
            return;
        }

        // Start server
        fakeMCServer.startServer();

        // Add shutdown hook
        Runtime.getRuntime().addShutdownHook(new Thread(fakeMCServer::stopServer, "Shutdown hook"));
    }

    private void printUsage() {
        System.out.println("Usage...: java -jar " + BuildUtils.getJarName(FakeMCServerCLI.class) + " [options...]");
        System.out.println("Example.: java -jar " + BuildUtils.getJarName(FakeMCServerCLI.class) + " -start -logs server.log");
        System.out.println();
        System.out.println("Options:");

        PrettyUsage prettyUsage = new PrettyUsage();
        prettyUsage.setPaddingLeft(3);
        prettyUsage.importValues(new JCommandArranger(jCommander).getOrderedParameters());

        for (String command : prettyUsage.getLines())
            System.out.println(command);
    }

    private JsonConfig handleConfig(FakeMCServer fakeMCServer) {
        try {
            Gson gson = new GsonBuilder().serializeNulls().setPrettyPrinting().create();

            if (jArguments.getConfigurationOverride() == null) {
                File file = new File(jArguments.getConfigurationPath());

                if (!file.exists()) {
                    Logs.info("No configuration file was found at path '" + jArguments.getConfigurationPath() + "'");
                    Logs.info("Creating one");

                    JsonConfig jsonConfig = new JsonConfig();

                    JsonSocketServer jsonSocketServer = new JsonSocketServer();
                    jsonSocketServer.setHost(fakeMCServer.getSocketHost());
                    jsonSocketServer.setPort(fakeMCServer.getSocketPort());
                    jsonConfig.setSocketServer(jsonSocketServer);

                    JsonProtocol jsonProtocol = new JsonProtocol();
                    jsonProtocol.setAllowAllClients(fakeMCServer.isAllowAllClients());
                    jsonProtocol.setProtocols(fakeMCServer.getAllowedProtocols());

                    JsonFakeServer jsonFakeServer = new JsonFakeServer();
                    jsonFakeServer.setServerIP(fakeMCServer.getFakeServerIp());
                    jsonFakeServer.setProtocol(jsonProtocol);
                    jsonFakeServer.setVersionName(fakeMCServer.getVersionName());
                    jsonFakeServer.setMotd(fakeMCServer.getMotd());
                    jsonFakeServer.setPlayers(fakeMCServer.getPlayers());
                    jsonFakeServer.setConnectedPlayers(fakeMCServer.getConnectedPlayers());
                    jsonFakeServer.setKickMessage(fakeMCServer.getKickMessage());
                    jsonFakeServer.setServerIcon(null);
                    jsonConfig.setFakeServer(jsonFakeServer);

                    String content = gson.toJson(jsonConfig, JsonConfig.class);
                    Files.writeString(file.toPath(), content);

                    return jsonConfig;
                }

                String content = Files.readString(file.toPath());

                return gson.fromJson(content, JsonConfig.class);
            } else {
                JsonConfig jsonConfig = gson.fromJson(jArguments.getConfigurationOverride(), JsonConfig.class);

                if (jsonConfig.getSocketServer() == null)
                    jsonConfig.setSocketServer(new JsonSocketServer());

                if (jsonConfig.getFakeServer() == null) {
                    JsonProtocol jsonProtocol = new JsonProtocol();
                    jsonProtocol.setProtocols(new ArrayList<>());
                    jsonProtocol.setAllowAllClients(true);

                    JsonFakeServer jsonFakeServer = new JsonFakeServer();
                    jsonFakeServer.setPlayers(new ArrayList<>());
                    jsonFakeServer.setProtocol(jsonProtocol);
                    jsonConfig.setFakeServer(jsonFakeServer);
                }

                return gson.fromJson(jArguments.getConfigurationOverride(), JsonConfig.class);
            }
        } catch (Exception exception) {
            Logs.severe("An error occurred while manipulating the configuration", exception);
            return null;
        }
    }

    private String getBase64img(String icon) throws Exception {
        if (icon == null)
            return null;

        if (icon.startsWith("data:image/png;base64")) {
            Logs.info("Using provided base64 as server icon");
            return icon;
        }

        Logs.info("Using image at '" + icon + "' as server icon");
        File file = new File(icon);

        if (!file.exists())
            throw new FileNotFoundException("No server icon was found at path '" + icon + "'");

        BufferedImage image = ImageIO.read(file);
        if (image == null)
            throw new IllegalArgumentException("The file at path '" + icon + "' is not a valid image.");

        if (!"png".equalsIgnoreCase(getFileExtension(icon)))
            throw new IllegalArgumentException("The image must be a PNG file.");

        if (image.getWidth() != 64 || image.getHeight() != 64)
            throw new IllegalArgumentException("The image must be 64x64 pixels.");

        byte[] imageBytes = Files.readAllBytes(file.toPath());

        return "data:image/png;base64," + Base64.getEncoder().encodeToString(imageBytes);
    }

    private String getFileExtension(String fileName) {
        int lastDotIndex = fileName.lastIndexOf('.');
        return (lastDotIndex == -1) ? "" : fileName.substring(lastDotIndex + 1);
    }

}