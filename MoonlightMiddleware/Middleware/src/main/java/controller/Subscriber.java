package controller;


import eu.quanticol.moonlight.domain.BooleanDomain;
import eu.quanticol.moonlight.signal.Signal;
import eu.quanticol.moonlight.signal.online.OnlineSignal;
import eu.quanticol.moonlight.signal.online.TimeChain;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import view.Screen;

import java.util.Arrays;

public class Subscriber implements MqttCallback {
    MQTT_handler mqtt_handler;
    Screen view;
    int size;

    public Subscriber(MQTT_handler mqtt_handler, Screen view, int size){
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
        String[] valuesStr = msg.split(" ");
        Integer[] numbers = new Integer[size];

        for(int i = 0;i < size;i++)
        {
            if(valuesStr.length <= i){
                numbers[i] = 0;
            }else{
                try
                {
                    numbers[i] = Integer.parseInt(valuesStr[i]);
                }
                catch (NumberFormatException nfe)
                {
                    numbers[i] = 0;
                }
            }
        }

        Thread thread = new Thread(){
            public void run(){
                System.out.println("Publishing...");
                mqtt_handler.publishList(Arrays.asList(numbers));
            }
        };
        thread.start();


    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {

    }
}
