package subscriber;

import controller.MainController;
import messages.Message;
import messages.OfficeMessage;
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

    //TODO: Think where to declare the specific message type (new OfficeMessage...)
    @Override
    public void receive(String topic, String message) {
        Message messageClass = new OfficeMessage();
        messageClass.transformReceivedData(topic, message);
        controller.updateData(messageClass);
    }
}
