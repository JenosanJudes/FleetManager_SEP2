package client.network;

// Observer-interface: klasser der vil informeres om server-push notifikationer
// implementerer dette interface (Observer-mønsteret fra SWD)
public interface ServerPushListener {
    void onPush(String event);
}
