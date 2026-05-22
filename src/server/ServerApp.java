package server;

import server.network.Server;

// Start-punkt for serveren
public class ServerApp {

    public static void main(String[] args) {
        System.out.println("Starter Fleet Manager server...");
        new Server().start();
    }
}
