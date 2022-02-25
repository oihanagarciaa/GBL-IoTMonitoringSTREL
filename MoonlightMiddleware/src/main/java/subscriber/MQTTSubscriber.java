package subscriber;

import controller.MainController;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttMessage;

public class MQTTSubscriber implements MqttCallback, Subscriber<String> {
    final private MainController controller;

    public MQTTSubscriber(String broker, MainController controller){
        this.controller = controller;
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
        //TODO: subscribe
    }

    @Override
    public void receive(String topic, String message) {
        controller.updateData(message);
    }
}
