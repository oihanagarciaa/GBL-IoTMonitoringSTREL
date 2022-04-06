package main;

import org.eclipse.paho.client.mqttv3.MqttException;
import subscriber.MQTTSubscriber;
import subscriber.MessageListener;
import subscriber.Subscriber;

class TMPConsumer implements MessageListener {
    Subscriber subscriber;
    String broker = "tcp://stefanschupp.de:1883";
    String topic = "institute/thingy/#";
    final static String USERNAME = "oihana";
    final static String PASSWORD = "22oihana22";

    public TMPConsumer () throws MqttException {
        this.subscriber = new MQTTSubscriber(broker, topic, USERNAME, PASSWORD);
        this.subscriber.addListener(this);
    }

    @Override
    public void messageArrived(String topic, String jsonMessage) {
        System.out.println(topic + " "+ jsonMessage);
    }

    public static void main(String[] args) throws MqttException {
        TMPConsumer main = new TMPConsumer();
    }
}
