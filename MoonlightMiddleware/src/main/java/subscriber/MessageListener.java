package subscriber;

public interface MessageListener {
    void messageArrived(String topic, String jsonMessage);
}
