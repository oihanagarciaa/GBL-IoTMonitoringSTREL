package thingsboard;

import messages.Message;
import connection.MQTTPublisher;

import java.util.Map;

public abstract class ThingsboardConnector {
    private final static int QOS = 0;

    public void publishToThingsboard(String broker, String topic, String username, String json){
        MQTTPublisher mqttPublisher = new MQTTPublisher(broker, topic, QOS);
        mqttPublisher.publish(username, "", json);
    }

    public String getUsername(Map<String, String> deviceAccessToken, String key) {
        return deviceAccessToken.get(key);
    }

    public abstract void sendMessage(Message message);
    public abstract String getJson();
}
