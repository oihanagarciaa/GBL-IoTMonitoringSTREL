package mycontrollers;


import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Subscriber implements MqttCallback {
    MQTT_handler mqtt_handler;
    ExecutorService executor;
    Controller controller;

    /*tmp*/ int i = 0;

    public Subscriber(MQTT_handler mqtt_handler, Controller controller){
        this.controller = controller;
        this.mqtt_handler = mqtt_handler;
        executor = Executors.newCachedThreadPool();
    }

    @Override
    public void connectionLost(Throwable cause) {

    }

    /*
    Is it good to use executors here?
     */
    @Override
    public void messageArrived(String topic, MqttMessage message) throws Exception {
        String msg = message.toString();
        System.out.println("msg: "+msg);

        executor.execute(() -> controller.convertSignalForMoonlight(msg));

        /*
        Change when to send the update
         */
        if(i==6) {
            mqtt_handler.publishUpdate(controller.getUpdate());
            i = 0;
        }else { i++;}
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {

    }
}
