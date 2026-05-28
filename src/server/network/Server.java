package server.network;

import shared.protocol.Response;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

// Lytter på en port og starter en ny tråd for hver klient der forbinder
public class Server {

    private static final int PORT = 8765;

    // Thread pool - håndterer op til 10 klienter på samme tid (NEC1)
    private final ExecutorService threadPool = Executors.newFixedThreadPool(10);

    // Liste over alle forbundne klienter - HashSet som i NEC1-eksemplerne
    private final Set<ClientHandler> connectedClients = new HashSet<>();

    public void start() {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server kører på port " + PORT);

            // Accepter klienter i en løkke
            while (true) {
                Socket clientSocket = serverSocket.accept();
                ClientHandler handler = new ClientHandler(clientSocket, this);
                synchronized (this) {
                    connectedClients.add(handler);
                }
                threadPool.execute(handler);
            }
        } catch (IOException e) {
            System.out.println("Server stoppet: " + e.getMessage());
        }
    }

    // Fjern en klient fra listen når den afbryder forbindelsen
    public synchronized void unregister(ClientHandler handler) {
        connectedClients.remove(handler);
        System.out.println("Klient fjernet. Aktive klienter: " + connectedClients.size());
    }

    // Send en push-notifikation til alle klienter undtagen afsenderen
    public void broadcast(Response push, ClientHandler sender) {
        // Lav en kopi af sættet så vi ikke holder låsen mens vi sender
        Set<ClientHandler> snapshot;
        synchronized (this) {
            snapshot = new HashSet<>(connectedClients);
        }
        for (ClientHandler client : snapshot) {
            if (client != sender) {
                client.pushToClient(push);
            }
        }
    }
}
