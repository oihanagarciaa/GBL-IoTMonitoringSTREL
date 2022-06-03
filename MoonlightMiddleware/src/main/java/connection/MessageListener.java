package connection;

public interface MessageListener {
    void messageArrived(String topic, String jsonMessage);
}
