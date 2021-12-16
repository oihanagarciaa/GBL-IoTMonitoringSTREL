package controller;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import view.Screen;

public class MQTT_handler {

    Screen view;
    private static final String clientId = "MyIoTClientID2";
    private static final String brokerUrl ="tcp://broker.mqttdashboard.com:1883";
    private static final String topicSubscriber = "iot/sensors";
    private static final String topicPublisher = "iot/moonlight";
    int qos = 0;
    MqttClient sampleClient;
    Subscriber sub;

    public MQTT_handler(Screen view){
        this.view = view;
        sub = new Subscriber(this.view);

        MemoryPersistence persistence = new MemoryPersistence();

        try {
            sampleClient = new MqttClient(brokerUrl, clientId, persistence);
            MqttConnectOptions connOpts = new MqttConnectOptions();
            connOpts.setCleanSession(true);

            sampleClient.connect(connOpts);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    public void subscribe(){
        try {
            sampleClient.setCallback(sub);
            sampleClient.subscribe(topicSubscriber);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    public void publishMessage(String message){
        try {
            MqttMessage mqttmessage = new MqttMessage(message.getBytes());
            mqttmessage.setQos(qos);
            sampleClient.publish(topicPublisher, mqttmessage);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

}
