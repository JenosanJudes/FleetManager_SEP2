package server.network;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

// Lytter på en port og starter en ny tråd for hver klient der forbinder
public class Server {

    private static final int PORT = 8765;

    // Thread pool - håndterer op til 10 klienter på samme tid
    private final ExecutorService threadPool = Executors.newFixedThreadPool(10);

    public void start() {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server kører på port " + PORT);

            // Accepter klienter i en løkke
            while (true) {
                Socket clientSocket = serverSocket.accept();
                // Giv klienten til thread pool'en så den håndteres i en ny tråd
                threadPool.execute(new ClientHandler(clientSocket));
            }
        } catch (IOException e) {
            System.out.println("Server stoppet: " + e.getMessage());
        }
    }
}
