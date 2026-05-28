package client.network;

import shared.protocol.Request;
import shared.protocol.Response;

import java.io.*;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

// Forbindelsen til serveren - sender requests og modtager responses + push-notifikationer
public class ServerConnection {

    private static final String HOST = "localhost";
    private static final int    PORT = 8765;

    private ObjectOutputStream out;
    private ObjectInputStream  in;

    // Lyttere der kaldes når serveren sender en push-notifikation
    private final List<Consumer<String>> pushListeners = new CopyOnWriteArrayList<>();

    // Den ventende response fra en send()-anmodning
    private volatile CompletableFuture<Response> pendingResponse;

    // Opret forbindelsen til serveren
    public void connect() throws IOException {
        Socket socket = new Socket(HOST, PORT);
        // OBS: ObjectOutputStream skal oprettes FØR ObjectInputStream
        out = new ObjectOutputStream(socket.getOutputStream());
        in  = new ObjectInputStream(socket.getInputStream());
        startReaderThread();
        System.out.println("Forbundet til serveren");
    }

    // Baggrundstråd der læser ALT fra serveren - både responses og push-notifikationer
    private void startReaderThread() {
        Thread reader = new Thread(() -> {
            while (true) {
                try {
                    Response response = (Response) in.readObject();

                    if (response.isPush()) {
                        // Server-push: informer alle lyttere (f.eks. ViewModels der genindlæser)
                        String event = response.getMessage();
                        for (Consumer<String> listener : pushListeners) {
                            listener.accept(event);
                        }
                    } else {
                        // Svar på vores egen request: fuldfør den ventende fremtid
                        CompletableFuture<Response> future = pendingResponse;
                        if (future != null) future.complete(response);
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

    // Send en request og vent på svaret (maks 10 sekunder)
    public synchronized Response send(Request request) throws Exception {
        CompletableFuture<Response> future = new CompletableFuture<>();
        pendingResponse = future;
        out.writeObject(request);
        out.flush();
        return future.get(10, TimeUnit.SECONDS);
    }

    // Tilmeld en lytter til server-push notifikationer
    // event vil være f.eks. "EMPLOYEES_UPDATED" eller "VEHICLES_UPDATED"
    public void addPushListener(Consumer<String> listener) {
        pushListeners.add(listener);
    }
}
