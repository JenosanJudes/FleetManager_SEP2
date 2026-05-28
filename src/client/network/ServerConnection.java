package client.network;

import shared.protocol.Request;
import shared.protocol.Response;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.*;
import java.net.Socket;

// Forbindelsen til serveren - sender requests og modtager responses + push-notifikationer
// Bruger PropertyChangeSupport til observer-mønsteret (som NEC1 Lektion 8)
public class ServerConnection {

    private static final String HOST = "localhost";
    private static final int    PORT = 8765;

    private ObjectOutputStream out;
    private ObjectInputStream  in;

    // PropertyChangeSupport bruges til at informere lyttere om server-push (NEC1 Observer-mønster)
    private final PropertyChangeSupport support = new PropertyChangeSupport(this);

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

    // Baggrundstråd der læser ALT indkommende fra serveren (NEC1 - receiver thread)
    private void startReaderThread() {
        Thread reader = new Thread(() -> {
            while (true) {
                try {
                    Response response = (Response) in.readObject();

                    if (response.isPush()) {
                        // Server-push: brug PropertyChangeSupport til at informere lyttere (NEC1 L8)
                        // Hændelsesnavnet er f.eks. "EMPLOYEES_UPDATED" eller "VEHICLES_UPDATED"
                        support.firePropertyChange(response.getMessage(), null, null);
                    } else {
                        // Svar på vores request: gem svaret og vågn send() op (NEC1 - wait/notify)
                        synchronized (this) {
                            lastResponse = response;
                            notifyAll();
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

    // Send en request og vent på svaret (NEC1 - synchronized + wait/notifyAll)
    public synchronized Response send(Request request) throws Exception {
        out.writeObject(request);
        out.flush();
        wait(); // Frigiver låsen og venter - reader-tråden kalder notifyAll() når svaret er klar
        return lastResponse;
    }

    // Tilmeld en lytter til et bestemt push-hændelse (NEC1 L8 - PropertyChangeListener)
    public void addListener(String eventName, PropertyChangeListener listener) {
        support.addPropertyChangeListener(eventName, listener);
    }
}
