package shared.protocol;

import java.io.Serializable;

// Det serveren sender tilbage til klienten
public class Response implements Serializable {

    private final boolean success;
    private final Object data;      // resultatet (f.eks. en liste af biler)
    private final String message;   // fejlbesked hvis noget gik galt

    private Response(boolean success, Object data, String message) {
        this.success = success;
        this.data = data;
        this.message = message;
    }

    // Bruges når det går godt - sender data med
    public static Response ok(Object data) {
        return new Response(true, data, null);
    }

    // Bruges når noget går galt - sender en fejlbesked med
    public static Response error(String message) {
        return new Response(false, null, message);
    }

    public boolean isSuccess() {
        return success;
    }

    public Object getData() {
        return data;
    }

    public String getMessage() {
        return message;
    }
}
