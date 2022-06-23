package connection;

public interface MessageListener {
    //TODO: I don't use topic, it can be quited
    void messageArrived(String topic, String jsonMessage);
}
