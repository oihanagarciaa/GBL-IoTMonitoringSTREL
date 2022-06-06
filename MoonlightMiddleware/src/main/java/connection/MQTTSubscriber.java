package connection;

import org.eclipse.paho.client.mqttv3.*;

public class MQTTSubscriber implements MqttCallback, Subscriber<String> {
    private MessageListener listener;
    protected final MQTTConnection mqttConnection;

    public MQTTSubscriber(String broker, String topic,
                          String username, String password){
        mqttConnection = new MQTTConnection(broker, username, password);
        subscribe(topic);
    }

    @Override
    public void connectionLost(Throwable cause) {
    }

    @Override
    public void messageArrived(String topic, MqttMessage message) {
        receive(topic, message.toString());
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {
    }

    @Override
    public void subscribe(String topic){
        try {
            MqttClient sampleClient = mqttConnection.getSampleClient();
            sampleClient.setCallback(this);
            sampleClient.subscribe(topic);
        } catch (MqttException e) {
        }
    }

    @Override
    public void addListener(MessageListener messageListener) {
        this.listener = messageListener;
    }

    @Override
    public void disconnect() {
        mqttConnection.disconnect();
    }

    @Override
    public void receive(String topic, String message) {
        listener.messageArrived(topic, message);
    }
}
