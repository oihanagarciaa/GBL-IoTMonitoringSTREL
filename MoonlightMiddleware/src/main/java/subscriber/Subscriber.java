package subscriber;

import org.eclipse.paho.client.mqttv3.MqttException;

public interface Subscriber<M> {

    void subscribe(String topic) throws MqttException;

    void receive(String topic, M message);

    void addListener(MessageListener messageListener);
}
