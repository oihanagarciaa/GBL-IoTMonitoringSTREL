package subscriber;

import controller.ServiceController;
import messages.Message;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import services.Service;

public class MQTTSubscriber implements MqttCallback, Subscriber<Message> {
    final private ServiceController controller;

    public MQTTSubscriber(String broker, ServiceController controller){
        this.controller = controller;
    }

    @Override
    public void connectionLost(Throwable cause) {

    }

    @Override
    public void messageArrived(String topic, MqttMessage message) throws Exception {
        receive(topic, fromMQTTtoMessage(message));
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {

    }

    @Override
    public void subscribe(String topic) {

    }

    @Override
    public void receive(String topic, Message message) {
        controller.updateData(message);
    }

    public Message fromMQTTtoMessage(MqttMessage mqttMessage){
        //TODO
        return null;
    }
}
