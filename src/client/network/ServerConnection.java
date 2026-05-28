package client.network;

import shared.protocol.Request;
import shared.protocol.Response;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

// Forbindelsen til serveren - sender requests og modtager responses + push-notifikationer
public class ServerConnection {

    private static final String HOST = "localhost";
    private static final int    PORT = 8765;

    private ObjectOutputStream out;
    private ObjectInputStream  in;

    // Observer-lyttere der informeres ved server-push (Observer-mønsteret)
    private final List<ServerPushListener> pushListeners = new ArrayList<>();

    // Den seneste response fra serveren (sættes af reader-tråden)
    private Response lastResponse;

    // Opret forbindelsen til serveren
    public void connect() throws IOException {
        Socket socket = new Socket(HOST, PORT);
        // OBS: ObjectOutputStream skal oprettes FØR ObjectInputStream
        out = new ObjectOutputStream(socket.getOutputStream());
        in  = new ObjectInputStream(socket.getInputStream());
        startReaderThread();
        System.out.println("Forbundet til serveren");
    }

    // Baggrundstråd der læser ALT indkommende fra serveren
    // Adskiller push-notifikationer fra svar på requests
    private void startReaderThread() {
        Thread reader = new Thread(() -> {
            while (true) {
                try {
                    Response response = (Response) in.readObject();

                    if (response.isPush()) {
                        // Server-push: informer alle Observer-lyttere
                        // Lav en kopi af listen så vi ikke holder låsen mens vi kalder lytterne
                        List<ServerPushListener> snapshot;
                        synchronized (this) {
                            snapshot = new ArrayList<>(pushListeners);
                        }
                        for (ServerPushListener listener : snapshot) {
                            listener.onPush(response.getMessage());
                        }
                    } else {
                        // Svar på vores request: gem svaret og vågn send() op
                        synchronized (this) {
                            lastResponse = response;
                            notifyAll(); // Vækker den ventende send()-metode
                        }
                    }

                } catch (Exception e) {
                    System.out.println("Forbindelsen til serveren mistet");
                    break;
                }
            }
        });
        reader.setDaemon(true); // Stopper automatisk når appen lukker
        reader.start();
    }

    // Send en request og vent på svaret
    // Bruger wait()/notifyAll() fra SDT1 i stedet for avanceret CompletableFuture
    public synchronized Response send(Request request) throws Exception {
        out.writeObject(request);
        out.flush();
        wait(); // Frigiver låsen og venter - reader-tråden kalder notifyAll() når svaret er klar
        return lastResponse;
    }

    // Tilmeld en Observer-lytter til server-push notifikationer
    public synchronized void addPushListener(ServerPushListener listener) {
        pushListeners.add(listener);
    }
}
