package controller;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import view.Screen;

public class Subscriber implements MqttCallback {

    Screen view;

    public Subscriber(Screen view){
        this.view = view;
    }

    @Override
    public void connectionLost(Throwable cause) {

    }

    @Override
    public void messageArrived(String topic, MqttMessage message) throws Exception {
        view.setRecievedMessage(message.toString());
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {

    }
}
