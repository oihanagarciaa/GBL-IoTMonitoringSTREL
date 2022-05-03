package subscriber;

import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.nio.charset.StandardCharsets;

public class MQTTPublisher {
    private final String broker;
    private final String topic;
    private final int qos;

    public MQTTPublisher(String broker, String topic, int qos){
        this.broker = broker;
        this.topic = topic;
        this.qos = qos;
    }

    public void publish(String username, String password, String message){
        try {
            MQTTConnection mqttConnection = new MQTTConnection(broker, username, password);
            MqttMessage mqttMessage = new MqttMessage(message.getBytes(StandardCharsets.UTF_8));
            mqttMessage.setQos(qos);
            mqttConnection.getSampleClient().publish(topic, mqttMessage);
            mqttConnection.disconnect();
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }
}
