package me.michidik.fakemcserver;

public class Main {

    public static void main(String[] args) {
        System.out.println("FakeMCServer version 1.2");
        System.out.println("https://github.com/alkanife/FakeMCServer");
        System.out.println("Original version by xxmicloxx, lorenzop and michidk");
        System.out.println("Use the 'debug' argument to enter debug mode");

        boolean debug = false;

        if (args.length > 0) {
            if (args[0].equalsIgnoreCase("help"))
                return;

            if (args[0].equalsIgnoreCase("debug"))
                debug = true;
        }

        System.out.println("----------------------------------------------------");

        new FakeMCServer(debug, true, "config.json");
    }

}
