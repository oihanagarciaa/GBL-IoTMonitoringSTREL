package subscriber;

import controller.MainController;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import java.util.UUID;

public class MQTTSubscriber implements MqttCallback, Subscriber<String> {
    //TODO: To change to a generic controller I have to add updateData(...) to the interface
    final private MainController controller;
    private static final String clientId = UUID.randomUUID().toString();
    private static int qos = 0;
    MqttClient sampleClient;

    public MQTTSubscriber(String broker, MainController controller){
        this.controller = controller;
        try(MemoryPersistence persistence = new MemoryPersistence()) {
            sampleClient = new MqttClient(broker, clientId, persistence);
            MqttConnectOptions connOpts = new MqttConnectOptions();
            connOpts.setCleanSession(true);

            sampleClient.connect(connOpts);
        } catch (MqttException ignored) {
        }
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
    public void subscribe(String topic) {
        try {
            sampleClient.setCallback(this);
            sampleClient.subscribe(topic);
        } catch (MqttException ignored) {
        }
    }

    //TODO: Is there a better way to extract the ID?
    @Override
    public void receive(String topic, String message) {
        String[] topics = topic.split("/");
        int id = Integer.parseInt(topics[topics.length-1]);
        controller.updateData(id, message);
    }
}
