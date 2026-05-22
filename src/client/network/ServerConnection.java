package client.network;

import shared.protocol.Request;
import shared.protocol.Response;

import java.io.*;
import java.net.Socket;

// Forbindelsen til serveren - sender requests og modtager responses
public class ServerConnection {

    private static final String HOST = "localhost";
    private static final int    PORT = 8765;

    private ObjectOutputStream out;
    private ObjectInputStream  in;

    // Opret forbindelsen til serveren
    public void connect() throws IOException {
        Socket socket = new Socket(HOST, PORT);
        // OBS: ObjectOutputStream skal oprettes FØR ObjectInputStream
        out = new ObjectOutputStream(socket.getOutputStream());
        in  = new ObjectInputStream(socket.getInputStream());
        System.out.println("Forbundet til serveren");
    }

    // Send en request og vent på svar (blokerende)
    public synchronized Response send(Request request) throws IOException, ClassNotFoundException {
        out.writeObject(request);
        out.flush();
        return (Response) in.readObject();
    }
}
