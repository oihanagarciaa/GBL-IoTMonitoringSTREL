package subscriber;

import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import java.util.UUID;

public class MQTTSubscriber implements MqttCallback, Subscriber<String> {
    private MessageListener listener;
    private static final String clientId = UUID.randomUUID().toString();
    private static int qos = 0;
    private static MqttClient sampleClient;

    public MQTTSubscriber(String broker, String topic, String username, String password) throws MqttException {
        try(MemoryPersistence persistence = new MemoryPersistence()) {
            sampleClient = new MqttClient(broker, MqttClient.generateClientId(), persistence);
            MqttConnectOptions connOpts = setUpConnectionOptions(username, password);
            sampleClient.connect(connOpts);
            subscribe(topic);
        }
    }

    private static MqttConnectOptions setUpConnectionOptions(String username, String password) {
        MqttConnectOptions connOpts = new MqttConnectOptions();
        connOpts.setCleanSession(true);
        connOpts.setUserName(username);
        connOpts.setPassword(password.toCharArray());
        return connOpts;
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
    public void subscribe(String topic) throws MqttException{
        sampleClient.setCallback(this);
        sampleClient.subscribe(topic);
    }

    @Override
    public void addListener(MessageListener messageListener) {
        this.listener = messageListener;
    }

    @Override
    public void receive(String topic, String message) {
        System.out.println("MESSAGE " + message);
        listener.messageArrived(topic, message);
    }
}
