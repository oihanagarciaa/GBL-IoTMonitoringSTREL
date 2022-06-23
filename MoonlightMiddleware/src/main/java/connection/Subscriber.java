package connection;

public interface Subscriber<M> {

    void subscribe(String topic);

    void receive(String topic, M message);

    void addListener(MessageListener messageListener);

    void disconnect();
}
