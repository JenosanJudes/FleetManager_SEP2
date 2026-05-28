package server.network;

import shared.protocol.Response;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

// Lytter på en port og starter en ny tråd for hver klient der forbinder
public class Server {

    private static final int PORT = 8765;

    // Thread pool - håndterer op til 10 klienter på samme tid
    private final ExecutorService threadPool = Executors.newFixedThreadPool(10);

    // Liste over alle forbundne klienter (CopyOnWriteArrayList er trådsikker)
    private final List<ClientHandler> connectedClients = new CopyOnWriteArrayList<>();

    public void start() {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server kører på port " + PORT);

            // Accepter klienter i en løkke
            while (true) {
                Socket clientSocket = serverSocket.accept();
                ClientHandler handler = new ClientHandler(clientSocket, this);
                connectedClients.add(handler);
                threadPool.execute(handler);
            }
        } catch (IOException e) {
            System.out.println("Server stoppet: " + e.getMessage());
        }
    }

    // Fjern en klient fra listen når den afbryder forbindelsen
    public void unregister(ClientHandler handler) {
        connectedClients.remove(handler);
        System.out.println("Klient fjernet. Aktive klienter: " + connectedClients.size());
    }

    // Send en push-notifikation til alle klienter undtagen afsenderen
    public void broadcast(Response push, ClientHandler sender) {
        for (ClientHandler client : connectedClients) {
            if (client != sender) {
                client.pushToClient(push);
            }
        }
    }
}
