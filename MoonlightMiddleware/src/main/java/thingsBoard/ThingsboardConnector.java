package thingsBoard;

import messages.Message;
import subscriber.MQTTPublisher;

public abstract class ThingsboardConnector {
    private final static String broker = "tcp://thingsboard.cloud:1883";
    private final static String topic = "v1/devices/me/telemetry";
    private final static int qos = 0;

    public void publishToThingsboard(String username, String password, String json){
        MQTTPublisher mqttPublisher = new MQTTPublisher(broker, topic, qos);
        mqttPublisher.publish(username, password, json);
    }

    public abstract void sendMessage(Message message);
    public abstract String getJson();
    public abstract String getUsername();
}
