package thingsBoard;

import messages.Message;
import connection.MQTTPublisher;

import java.util.Map;

public abstract class ThingsboardConnector {
    private final static String broker = "tcp://thingsboard.stefanschupp.de:1884";
    private final static String topic = "v1/devices/me/telemetry";
    private final static int qos = 0;

    public void publishToThingsboard(String username, String json){
        MQTTPublisher mqttPublisher = new MQTTPublisher(broker, topic, qos);
        mqttPublisher.publish(username, "", json);
    }

    public String getUsername(Map<String, String> deviceAccessToken, String key) {
        String username = deviceAccessToken.get(key);
        return username;
    }

    public abstract void sendMessage(Message message);
    public abstract String getJson();
}
