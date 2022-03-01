package subscriber;

import controller.MainController;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import java.util.UUID;

public class MQTTSubscriber implements MqttCallback, Subscriber<String> {
    final private MainController controller;
    private static final String clientId = UUID.randomUUID().toString();
    private static final String topicSubscriber = "institute/thingy/#";
    private static int qos = 0;
    MqttClient sampleClient;

    public MQTTSubscriber(String broker, MainController controller){
        this.controller = controller;
        try(MemoryPersistence persistence = new MemoryPersistence()) {
            sampleClient = new MqttClient(broker, clientId, persistence);
            MqttConnectOptions connOpts = new MqttConnectOptions();
            connOpts.setCleanSession(true);

            sampleClient.connect(connOpts);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void connectionLost(Throwable cause) {

    }

    @Override
    public void messageArrived(String topic, MqttMessage message) throws Exception {
        receive(topic, message.toString());
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {

    }

    @Override
    public void subscribe(String topic) {
        try {
            sampleClient.setCallback(this);
            sampleClient.subscribe(topicSubscriber);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void receive(String topic, String message) {
        System.out.println("Topic-> "+topic);
        String topics[] = topic.split("/");
        int id = Integer.parseInt(topics[topics.length-1]);
        System.out.println("ID: "+id);
        controller.updateData(id, message);
        //TODO: Quit the print line
        System.out.println("- Results: "+controller.getResults());
    }
}
