package me.michidik.fakemcserver;

public class Main {

    public static void main(String[] args) {
        System.out.println("FakeMCServer version 1.2.1");
        System.out.println("https://github.com/alkanife/FakeMCServer");
        System.out.println("Original version by xxmicloxx, lorenzop and michidk");

        try {
            boolean debug = false;
            boolean verbose = false;
            String configPath = "config.json";

            if (args.length > 0) {
                if (args[0].equalsIgnoreCase("version"))
                    return;

                debug = Boolean.parseBoolean(args[0]);

                if (args.length > 1) {
                    verbose = Boolean.parseBoolean(args[1]);

                    if (args.length > 2)
                        configPath = args[2];
                }
            }

            System.out.println("----------------------------------------------------");

            FakeMCServer fakeMCServer = new FakeMCServer(null);
            fakeMCServer.setupDefaultLogger();
            fakeMCServer.setDebug(debug);
            fakeMCServer.debug("Debug mode enabled");
            fakeMCServer.setVerbose(verbose);
            fakeMCServer.verbose("Verbose on");
            fakeMCServer.loadConfiguration(configPath);
            fakeMCServer.addShutdownHook();
            fakeMCServer.startServer();
        } catch (Exception exception) {
            exception.printStackTrace();
            System.exit(0);
        }
    }

}
