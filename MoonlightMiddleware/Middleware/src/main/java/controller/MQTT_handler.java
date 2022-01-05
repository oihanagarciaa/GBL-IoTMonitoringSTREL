package controller;

import eu.quanticol.moonlight.signal.SpatialTemporalSignal;
import eu.quanticol.moonlight.space.MoonLightRecord;
import model.SignalDefinition;
import org.apache.commons.lang3.SerializationUtils;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import view.Screen;

import java.util.Arrays;

public class MQTT_handler {

    Screen view;
    private static final String clientId = "MyIoTClientID2";
    private static final String brokerUrl ="tcp://broker.mqttdashboard.com:1883";
    private static final String topicSubscriber = "iot/sensors";
    private static final String topicPublisher = "iot/moonlight";
    int qos = 0;
    MqttClient sampleClient;
    Subscriber sub;
    SignalDefinition signalDefinition;

    public MQTT_handler(Screen view, SignalDefinition signalDefinition, int size){
        this.view = view;
        this.signalDefinition = signalDefinition;
        sub = new Subscriber(this, this.view, this.signalDefinition, size);

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

    public void publishMessage(int size, String msg){ /*SpatialTemporalSignal now implements serializable*/
        String[] valuesStr = msg.split(" ");
        Integer[] numbers = new Integer[size];
        for(int i = 0;i < size;i++)
        {
            try
            {
                numbers[i] = Integer.parseInt(valuesStr[i]);
            }
            catch (NumberFormatException nfe)
            {
                numbers[i] = null;
            }
        }
        try {
            System.out.println("Publishing...");
            MqttMessage mqttmessage = new MqttMessage(SerializationUtils.serialize(signalDefinition.setUpSignal(Arrays.asList(numbers))));
            mqttmessage.setQos(qos);
            sampleClient.publish(topicPublisher, mqttmessage);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }
}
