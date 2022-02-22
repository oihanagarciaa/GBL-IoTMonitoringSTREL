package subscriber;


public interface Subscriber<M> {

    void subscribe(String topic);

    void receive(String topic, M message);
}
