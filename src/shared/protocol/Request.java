package shared.protocol;

import java.io.Serializable;

// Det klienten sender til serveren
public class Request implements Serializable {

    private final RequestType type;
    private final Object data;  // det der sendes med (f.eks. et Vehicle-objekt eller en String)

    public Request(RequestType type, Object data) {
        this.type = type;
        this.data = data;
    }

    public RequestType getType() {
        return type;
    }

    public Object getData() {
        return data;
    }
}
