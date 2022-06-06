package main;

import connection.MQTTPublisher;
import connection.MQTTSubscriber;
import connection.MessageListener;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import java.util.concurrent.TimeUnit;

public class tmp implements MessageListener {
    public tmp() throws InterruptedException {
        String broker = "tcp://stefanschupp.de:1883";
        String topic = "v1/board/runner";
        String username = "oihana";
        String password = "22oihana22";
        //Topic name
        //data to be send
        String content      = "Hello :)";

        MQTTSubscriber mqttSubscriber = new MQTTSubscriber(broker, topic, username, password);
        mqttSubscriber.addListener(this);
        /*MQTTPublisher mqttPublisher = new MQTTPublisher(broker, topic, 0);
        mqttPublisher.publish(username, password, content);
        TimeUnit.SECONDS.sleep(5);
        mqttPublisher.publish(username, password, "Bye");*/
    }
    public static void main(String[] args) {
        try {
            tmp tmp= new tmp();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void messageArrived(String topic, String jsonMessage) {
        System.out.println("-> "+jsonMessage);
    }
}
