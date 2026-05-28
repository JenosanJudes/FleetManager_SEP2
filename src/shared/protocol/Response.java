package shared.protocol;

import java.io.Serializable;

// Det serveren sender tilbage til klienten
public class Response implements Serializable {

    private final boolean success;
    private final boolean push;     // true = server-push notifikation (ikke svar på en request)
    private final Object data;      // resultatet (f.eks. en liste af biler)
    private final String message;   // fejlbesked eller push-hændelsesnavn

    private Response(boolean success, boolean push, Object data, String message) {
        this.success = success;
        this.push    = push;
        this.data    = data;
        this.message = message;
    }

    // Bruges når det går godt - sender data med
    public static Response ok(Object data) {
        return new Response(true, false, data, null);
    }

    // Bruges når noget går galt - sender en fejlbesked med
    public static Response error(String message) {
        return new Response(false, false, null, message);
    }

    // Server-push notifikation til alle andre klienter
    // event = f.eks. "EMPLOYEES_UPDATED" eller "VEHICLES_UPDATED"
    public static Response push(String event) {
        return new Response(true, true, null, event);
    }

    public boolean isSuccess()  { return success; }
    public boolean isPush()     { return push; }
    public Object getData()     { return data; }
    public String getMessage()  { return message; }
}
