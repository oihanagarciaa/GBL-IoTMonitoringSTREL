package controller;

import model.SignalDefinition;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import view.Screen;

import java.util.ArrayList;
import java.util.Arrays;

public class Subscriber implements MqttCallback {
    SignalDefinition signalDefinition;
    MQTT_handler mqtt_handler;
    Screen view;
    int size;

    public Subscriber(MQTT_handler mqtt_handler, Screen view, SignalDefinition signalDefinition, int size){
        this.signalDefinition = signalDefinition;
        this.view = view;
        this.size = size;
        this.mqtt_handler = mqtt_handler;
    }

    @Override
    public void connectionLost(Throwable cause) {

    }

    @Override
    public void messageArrived(String topic, MqttMessage message) throws Exception {
        System.out.println("Recivido");
        String msg = message.toString();
        view.setRecievedMessage(message.toString());
        /*String[] valuesStr = msg.split(" ");
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
        }*/
        //System.out.println("Publish");
        mqtt_handler.publishMessage(/*signalDefinition.setUpSignal(Arrays.asList(numbers))*/"HOLA");
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {

    }
}
